package com.captainslog.sync

/**
 * Interface for type-specific sync handlers.
 * Each implementation handles bidirectional sync for one data type.
 */
interface SyncHandler {
    val dataType: DataType

    /** Sync local unsynced data to server */
    suspend fun syncToServer(): HandlerSyncResult

    /** Download data from server */
    suspend fun syncFromServer(): HandlerSyncResult

    /** Full bidirectional sync */
    suspend fun sync(): HandlerSyncResult {
        val toServer = syncToServer()
        val fromServer = syncFromServer()
        return HandlerSyncResult(
            success = toServer.success && fromServer.success,
            syncedCount = toServer.syncedCount + fromServer.syncedCount,
            conflictCount = toServer.conflictCount + fromServer.conflictCount,
            errors = toServer.errors + fromServer.errors
        )
    }

    /** Sync a specific entity by ID (for immediate sync) */
    suspend fun syncEntity(entityId: String): HandlerSyncResult
}

/**
 * Result of a sync handler operation.
 * Named HandlerSyncResult to avoid conflict with existing SyncResult sealed class
 * used by TemplateSyncWorker.
 */
/**
 * Data types that can be synced.
 * Replaces the DataType enum previously in ComprehensiveSyncManager.
 * TEMPLATES combines maintenance templates and events.
 * LOCATIONS replaces MARKED_LOCATIONS.
 */
enum class DataType {
    BOATS,
    TRIPS,
    NOTES,
    TODOS,
    TEMPLATES,
    LOCATIONS,
    PHOTOS
}

/**
 * Sync progress information for UI display.
 */
data class SyncProgress(
    val message: String,
    val current: Int,
    val total: Int
) {
    val percentage: Int get() = if (total > 0) (current * 100) / total else 0
}

/**
 * Data class representing a sync conflict for UI display.
 * Used by SyncConflictScreen and SyncOrchestrator.
 */
data class SyncConflict(
    val id: String,
    val entityType: String,
    val entityId: String,
    val localTimestamp: java.util.Date,
    val serverTimestamp: java.util.Date,
    val description: String
)

data class HandlerSyncResult(
    val success: Boolean = true,
    val syncedCount: Int = 0,
    val conflictCount: Int = 0,
    val errors: List<String> = emptyList()
)
