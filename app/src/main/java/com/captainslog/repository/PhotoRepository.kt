package com.captainslog.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.PhotoEntity
import com.captainslog.network.models.PhotoResponse
import com.captainslog.sync.ImmediateSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

/**
 * Repository for managing photos locally and syncing with the backend
 * Automatically syncs photo metadata immediately, uploads files on WiFi
 */
class PhotoRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val photoDao = database.photoDao()
    private val connectionManager = ConnectionManager.getInstance(context)
    private val immediateSyncService = ImmediateSyncService.getInstance(context, database)

    companion object {
        const val TAG = "PhotoRepository"
        const val PHOTOS_DIR = "photos"
        const val RETENTION_DAYS = 7L
    }

    /**
     * Save a photo locally from camera or gallery
     */
    suspend fun savePhoto(
        entityType: String,
        entityId: String,
        imageUri: Uri,
        mimeType: String
    ): PhotoEntity = withContext(Dispatchers.IO) {
        try {
            // Create photos directory if it doesn't exist
            val photosDir = File(context.filesDir, PHOTOS_DIR)
            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }

            // Generate unique filename
            val timestamp = System.currentTimeMillis()
            val extension = getExtensionFromMimeType(mimeType)
            val filename = "${entityType}_${entityId}_${timestamp}.${extension}"
            val localFile = File(photosDir, filename)

            // Copy image data to local file
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                FileOutputStream(localFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IllegalStateException("Could not open image URI")

            // Create photo entity
            val photoEntity = PhotoEntity(
                entityType = entityType,
                entityId = entityId,
                localPath = localFile.absolutePath,
                mimeType = mimeType,
                sizeBytes = localFile.length(),
                uploaded = false,
                uploadedAt = null,
                createdAt = Date()
            )

            // Save to database
            photoDao.insertPhoto(photoEntity)
            
            // Sync metadata immediately if connected, upload file only on WiFi
            immediateSyncService.syncPhoto(photoEntity.id)
            
            Log.d(TAG, "Photo saved locally: ${photoEntity.id}")
            photoEntity
        } catch (e: Exception) {
            Log.e(TAG, "Error saving photo", e)
            throw e
        }
    }

    /**
     * Get photos for a specific entity
     */
    fun getPhotosForEntity(entityType: String, entityId: String): Flow<List<PhotoEntity>> {
        return photoDao.getPhotosForEntity(entityType, entityId)
    }

    /**
     * Get all unuploaded photos
     */
    suspend fun getUnuploadedPhotos(): List<PhotoEntity> = withContext(Dispatchers.IO) {
        photoDao.getUnuploadedPhotos()
    }

    /**
     * Upload a photo to the backend
     * This should only be called when on WiFi and preferably on local connection
     */
    suspend fun uploadPhoto(photo: PhotoEntity): Result<PhotoResponse> = withContext(Dispatchers.IO) {
        try {
            // Check if we're on WiFi (requirement: WiFi-only uploads)
            if (!connectionManager.isOnWiFi()) {
                Log.d(TAG, "Not on WiFi, skipping photo upload for ${photo.id}")
                return@withContext Result.failure(Exception("Photo uploads only allowed on WiFi"))
            }

            // Initialize connection manager
            connectionManager.initialize()
            
            // Prefer local connection for photo uploads to save bandwidth
            val apiService = connectionManager.getApiService()

            val file = File(photo.localPath)
            if (!file.exists()) {
                Log.e(TAG, "Photo file not found: ${photo.localPath}")
                return@withContext Result.failure(Exception("Photo file not found"))
            }

            // Create multipart request
            val requestFile = file.asRequestBody(photo.mimeType.toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val entityTypePart = photo.entityType.toRequestBody("text/plain".toMediaTypeOrNull())
            val entityIdPart = photo.entityId.toRequestBody("text/plain".toMediaTypeOrNull())

            // Upload photo
            val response = apiService.uploadPhoto(entityTypePart, entityIdPart, photoPart)

            if (response.isSuccessful) {
                val photoResponse = response.body()
                if (photoResponse != null) {
                    // Mark as uploaded
                    photoDao.markAsUploaded(photo.id, Date())
                    
                    Log.d(TAG, "Photo uploaded successfully: ${photo.id} -> ${photoResponse.id}")
                    Log.d(TAG, "Used connection type: ${connectionManager.getCurrentConnectionType()}")
                    
                    return@withContext Result.success(photoResponse)
                } else {
                    Log.e(TAG, "Upload response body is null for photo ${photo.id}")
                    return@withContext Result.failure(Exception("Upload response body is null"))
                }
            } else {
                Log.e(TAG, "Photo upload failed: ${response.code()} - ${response.message()}")
                return@withContext Result.failure(Exception("Upload failed: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading photo ${photo.id}", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Clean up old uploaded photos (7-day retention)
     */
    suspend fun cleanupOldPhotos(): Int = withContext(Dispatchers.IO) {
        try {
            val cutoffDate = Date(System.currentTimeMillis() - (RETENTION_DAYS * 24 * 60 * 60 * 1000))
            val oldPhotos = photoDao.getOldUploadedPhotos(cutoffDate)
            
            var deletedCount = 0
            for (photo in oldPhotos) {
                try {
                    // Delete local file
                    val file = File(photo.localPath)
                    if (file.exists() && file.delete()) {
                        Log.d(TAG, "Deleted old photo file: ${photo.localPath}")
                    }
                    
                    // Delete database record
                    photoDao.deletePhoto(photo)
                    deletedCount++
                    
                    Log.d(TAG, "Cleaned up old photo: ${photo.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error cleaning up photo ${photo.id}", e)
                }
            }
            
            Log.d(TAG, "Cleaned up $deletedCount old photos")
            deletedCount
        } catch (e: Exception) {
            Log.e(TAG, "Error during photo cleanup", e)
            0
        }
    }

    /**
     * Delete a photo (both local file and database record)
     */
    suspend fun deletePhoto(photo: PhotoEntity) = withContext(Dispatchers.IO) {
        try {
            // Delete local file
            val file = File(photo.localPath)
            if (file.exists()) {
                file.delete()
            }
            
            // Delete database record
            photoDao.deletePhoto(photo)
            
            Log.d(TAG, "Deleted photo: ${photo.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting photo ${photo.id}", e)
            throw e
        }
    }

    /**
     * Get file extension from MIME type
     */
    private fun getExtensionFromMimeType(mimeType: String): String {
        return when (mimeType.lowercase()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            "image/bmp" -> "bmp"
            "image/tiff" -> "tiff"
            else -> "jpg"
        }
    }

    /**
     * Get the count of unuploaded photos
     */
    suspend fun getUnuploadedPhotoCount(): Int = withContext(Dispatchers.IO) {
        getUnuploadedPhotos().size
    }

    /**
     * Check if a photo file exists locally
     */
    suspend fun photoFileExists(photo: PhotoEntity): Boolean = withContext(Dispatchers.IO) {
        File(photo.localPath).exists()
    }
}