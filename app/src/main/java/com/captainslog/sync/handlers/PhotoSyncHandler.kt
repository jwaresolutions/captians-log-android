package com.captainslog.sync.handlers

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.network.NetworkMonitor
import com.captainslog.repository.PhotoRepository
import com.captainslog.sync.DataType
import com.captainslog.sync.HandlerSyncResult
import com.captainslog.sync.SyncHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoSyncHandler @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val connectionManager: ConnectionManager,
    private val networkMonitor: NetworkMonitor,
    private val database: AppDatabase
) : SyncHandler {

    companion object {
        private const val TAG = "PhotoSyncHandler"
    }

    override val dataType = DataType.PHOTOS

    override suspend fun syncFromServer(): HandlerSyncResult {
        // TODO: Implement photo metadata sync FROM server
        // Currently PhotoRepository only handles uploads TO server
        Log.d(TAG, "Photo sync from server - metadata download not yet implemented")
        return HandlerSyncResult(success = true)
    }

    override suspend fun syncToServer(): HandlerSyncResult {
        return try {
            val unuploadedPhotos = photoRepository.getUnuploadedPhotos()
            Log.d(TAG, "Found ${unuploadedPhotos.size} unuploaded photos")

            if (unuploadedPhotos.isEmpty()) {
                return HandlerSyncResult(success = true)
            }

            // Photo file uploads require WiFi
            if (!connectionManager.isOnWiFi()) {
                Log.d(TAG, "Not on WiFi, skipping photo file uploads")
                return HandlerSyncResult(success = true)
            }

            var successCount = 0
            val errors = mutableListOf<String>()

            for (photo in unuploadedPhotos) {
                try {
                    if (!photoRepository.photoFileExists(photo)) {
                        Log.w(TAG, "Photo file not found, removing: ${photo.id}")
                        photoRepository.deletePhoto(photo)
                        continue
                    }

                    val result = photoRepository.uploadPhoto(photo)
                    if (result.isSuccess) {
                        successCount++
                        Log.d(TAG, "Uploaded photo: ${photo.id}")
                    } else {
                        errors.add("Failed to upload photo ${photo.id}: ${result.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    errors.add("Error uploading photo ${photo.id}: ${e.message}")
                }
            }

            // Cleanup old uploaded photos
            try {
                photoRepository.cleanupOldPhotos()
            } catch (e: Exception) {
                Log.e(TAG, "Error during photo cleanup", e)
            }

            HandlerSyncResult(
                success = errors.isEmpty() || successCount > 0,
                syncedCount = successCount,
                errors = errors
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing photos to server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncEntity(entityId: String): HandlerSyncResult {
        return try {
            val photo = database.photoDao().getPhotoById(entityId) ?: return HandlerSyncResult(
                success = false, errors = listOf("Photo not found: $entityId")
            )

            if (!networkMonitor.canSyncData()) {
                Log.d(TAG, "No connection, photo will sync later: $entityId")
                return HandlerSyncResult(success = true) // Not a failure, just deferred
            }

            // Upload file only on WiFi
            if (networkMonitor.canUploadPhotos()) {
                val result = photoRepository.uploadPhoto(photo)
                if (result.isSuccess) {
                    Log.d(TAG, "Photo uploaded: $entityId")
                    HandlerSyncResult(success = true, syncedCount = 1)
                } else {
                    HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Upload failed"))
                }
            } else {
                Log.d(TAG, "Not on WiFi, photo file will upload later: $entityId")
                HandlerSyncResult(success = true) // Metadata only for now
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing photo entity: $entityId", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }
}
