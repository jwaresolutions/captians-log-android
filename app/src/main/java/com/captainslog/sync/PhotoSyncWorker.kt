package com.captainslog.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WorkManager worker for syncing photos to the backend API.
 * Only uploads photos when on WiFi connection and prefers local connection.
 * Handles 7-day retention cleanup after successful upload.
 */
class PhotoSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val database = AppDatabase.getInstance(context)
    private val photoRepository = PhotoRepository(database, context)
    private val connectionManager = ConnectionManager.getInstance(context)
    private val syncStatusManager = SyncStatusManager.getInstance(context)

    companion object {
        const val TAG = "PhotoSyncWorker"
        const val WORK_NAME = "photo_sync_work"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting photo sync...")

            // Check if we have internet connection
            if (!connectionManager.hasInternetConnection()) {
                Log.d(TAG, "No internet connection, will retry later")
                // Don't report as failure for photos since they have different requirements
                return@withContext Result.retry()
            }

            // Check if we're on WiFi (requirement: WiFi-only uploads)
            if (!connectionManager.isOnWiFi()) {
                Log.d(TAG, "Not on WiFi, skipping photo sync")
                // Don't report as failure - this is expected behavior
                return@withContext Result.success() // Success because we don't want to retry on mobile data
            }

            // Get all unuploaded photos
            val unuploadedPhotos = photoRepository.getUnuploadedPhotos()
            Log.d(TAG, "Found ${unuploadedPhotos.size} unuploaded photos")

            if (unuploadedPhotos.isEmpty()) {
                Log.d(TAG, "No photos to upload")
                
                // Still run cleanup even if no photos to upload
                val cleanedCount = photoRepository.cleanupOldPhotos()
                Log.d(TAG, "Cleaned up $cleanedCount old photos")
                
                return@withContext Result.success()
            }

            var successCount = 0
            var failureCount = 0

            // Upload each photo
            for (photo in unuploadedPhotos) {
                try {
                    Log.d(TAG, "Uploading photo ${photo.id} for ${photo.entityType}:${photo.entityId}")

                    // Check if photo file still exists
                    if (!photoRepository.photoFileExists(photo)) {
                        Log.w(TAG, "Photo file not found, removing from database: ${photo.id}")
                        photoRepository.deletePhoto(photo)
                        continue
                    }

                    // Upload photo (this will check WiFi again and prefer local connection)
                    val result = photoRepository.uploadPhoto(photo)
                    
                    if (result.isSuccess) {
                        val photoResponse = result.getOrNull()
                        Log.d(TAG, "Successfully uploaded photo ${photo.id} -> server ID: ${photoResponse?.id}")
                        Log.d(TAG, "Connection type used: ${connectionManager.getCurrentConnectionType()}")
                        successCount++
                    } else {
                        val error = result.exceptionOrNull()
                        Log.e(TAG, "Failed to upload photo ${photo.id}: ${error?.message}")
                        
                        // If it's a WiFi requirement error, don't count as failure
                        if (error?.message?.contains("WiFi") == true) {
                            Log.d(TAG, "WiFi requirement not met, will retry later")
                            return@withContext Result.success()
                        }
                        
                        failureCount++
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading photo ${photo.id}: ${e.message}", e)
                    failureCount++
                }
            }

            // Clean up old uploaded photos (7-day retention)
            try {
                val cleanedCount = photoRepository.cleanupOldPhotos()
                Log.d(TAG, "Cleaned up $cleanedCount old photos")
            } catch (e: Exception) {
                Log.e(TAG, "Error during photo cleanup", e)
            }

            Log.d(TAG, "Photo sync complete: $successCount succeeded, $failureCount failed")

            // Photos have different success criteria - don't affect main sync status unless critical failure
            if (failureCount > 0 && successCount == 0 && unuploadedPhotos.size > 5) {
                // Only report photo failures if many photos failed and none succeeded
                syncStatusManager.reportSyncFailure("Failed to upload $failureCount photo(s)")
            }

            // Return success if at least some photos uploaded, retry if all failed
            return@withContext if (successCount > 0 || failureCount == 0) {
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Fatal error during photo sync: ${e.message}", e)
            return@withContext Result.retry()
        }
    }
}