package com.captainslog.sync.handlers

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.repository.MarkedLocationRepository
import com.captainslog.sync.DataType
import com.captainslog.sync.HandlerSyncResult
import com.captainslog.sync.SyncHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationSyncHandler @Inject constructor(
    private val markedLocationRepository: MarkedLocationRepository,
    private val connectionManager: ConnectionManager
) : SyncHandler {

    companion object {
        private const val TAG = "LocationSyncHandler"
    }

    override val dataType = DataType.LOCATIONS

    override suspend fun syncFromServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing marked locations from server...")
            val result = markedLocationRepository.syncMarkedLocationsFromApi()
            if (result.isFailure) {
                Log.w(TAG, "Failed to sync locations from server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                Log.d(TAG, "Location sync from server completed")
                HandlerSyncResult(success = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing locations from server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncToServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing marked locations to server...")
            val result = markedLocationRepository.syncUnsyncedMarkedLocations()
            if (result.isFailure) {
                Log.w(TAG, "Failed to sync locations to server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                Log.d(TAG, "Location sync to server completed")
                HandlerSyncResult(success = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing locations to server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncEntity(entityId: String): HandlerSyncResult {
        // Delegate to full sync to server - individual location sync not yet supported
        Log.d(TAG, "Syncing location entity: $entityId (via full sync)")
        return syncToServer()
    }
}
