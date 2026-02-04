package com.captainslog.sync

import android.content.Context
import android.util.Log
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.*
import com.captainslog.database.entities.*
import com.captainslog.mode.AppModeManager
import com.captainslog.security.SecurePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Manages disconnection from the server with data ownership rules.
 *
 * Data retention rules on disconnect:
 * - Boats: Keep owned boats (ownerId == userId), delete others
 * - Trips: Keep captain trips, mark crew trips read-only, delete others
 * - Notes: Keep created by user (originSource == null or no server owner), delete others
 * - Photos: Keep associated with kept trips/boats, delete orphaned
 * - Todos: Keep all (assumed user-created in current version)
 * - Marked Locations: Keep all (assumed user-created in current version)
 * - Maintenance Templates/Events: Keep for owned boats, delete for other boats
 */
class DisconnectManager @Inject constructor(
    private val database: AppDatabase,
    private val securePreferences: SecurePreferences,
    private val appModeManager: AppModeManager,
    private val context: Context
) {
    companion object {
        private const val TAG = "DisconnectManager"
    }

    /**
     * Perform disconnection with optional final sync.
     *
     * @param downloadFirst If true, perform a full sync before disconnecting
     * @return DisconnectResult with statistics about data kept/removed
     */
    suspend fun disconnect(downloadFirst: Boolean): DisconnectResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting disconnect process (downloadFirst=$downloadFirst)")

            // Step 1: Optionally sync all data first
            if (downloadFirst) {
                Log.d(TAG, "Performing final sync before disconnect...")
                val syncManager = ComprehensiveSyncManager.getInstance(context, database)
                syncManager.performFullSync()

                // Wait for sync to complete
                while (syncManager.isSyncInProgress()) {
                    kotlinx.coroutines.delay(100)
                }
                Log.d(TAG, "Final sync completed")
            }

            // Step 2: Get current user ID (we need this before clearing credentials)
            val currentUserId = extractUserIdFromToken(securePreferences.jwtToken)
            Log.d(TAG, "Current user ID: $currentUserId")

            if (currentUserId == null) {
                Log.w(TAG, "No valid user ID found, treating all data as locally owned")
            }

            // Step 3: Process boats
            val boatStats = processBoats(currentUserId)
            Log.d(TAG, "Boats: kept=${boatStats.kept}, removed=${boatStats.removed}")

            // Step 4: Process trips
            val tripStats = processTrips(currentUserId)
            Log.d(TAG, "Trips: kept=${tripStats.kept}, readOnly=${tripStats.markedReadOnly}, removed=${tripStats.removed}")

            // Step 5: Process notes
            val noteStats = processNotes(currentUserId)
            Log.d(TAG, "Notes: kept=${noteStats.kept}, removed=${noteStats.removed}")

            // Step 6: Process photos (clean up orphaned)
            val photoStats = processPhotos()
            Log.d(TAG, "Photos: kept=${photoStats.kept}, removed=${photoStats.removed}")

            // Step 7: Process maintenance data
            val maintenanceStats = processMaintenanceData()
            Log.d(TAG, "Maintenance: kept=${maintenanceStats.kept}, removed=${maintenanceStats.removed}")

            // Step 8: Process todos and marked locations (keep all for now)
            val todoStats = processTodos()
            val locationStats = processMarkedLocations()
            Log.d(TAG, "Todos: kept=${todoStats.kept}, Locations: kept=${locationStats.kept}")

            // Step 9: Clear server credentials and configuration
            clearServerConfiguration()

            // Step 10: Refresh app mode to standalone
            appModeManager.refresh()

            val result = DisconnectResult(
                boatsKept = boatStats.kept,
                boatsRemoved = boatStats.removed,
                tripsKept = tripStats.kept,
                tripsMarkedReadOnly = tripStats.markedReadOnly,
                tripsRemoved = tripStats.removed,
                notesKept = noteStats.kept,
                notesRemoved = noteStats.removed,
                photosKept = photoStats.kept,
                photosRemoved = photoStats.removed,
                todosKept = todoStats.kept,
                locationsKept = locationStats.kept,
                maintenanceKept = maintenanceStats.kept,
                maintenanceRemoved = maintenanceStats.removed
            )

            Log.d(TAG, "Disconnect completed successfully: $result")
            result

        } catch (e: Exception) {
            Log.e(TAG, "Error during disconnect", e)
            throw DisconnectException("Failed to disconnect: ${e.message}", e)
        }
    }

    /**
     * Extract user ID from JWT token.
     * In a real implementation, you'd decode the JWT.
     * For now, we check if we have stored user info.
     */
    private fun extractUserIdFromToken(token: String?): String? {
        // TODO: In production, decode JWT and extract user ID from claims
        // For now, we'll rely on the username as a proxy, but we should
        // store userId separately when we receive LoginResponse
        return securePreferences.username
    }

    /**
     * Process boats: keep owned, delete others
     */
    private suspend fun processBoats(currentUserId: String?): DataStats {
        val allBoats = database.boatDao().getAllBoatsSync()
        var kept = 0
        var removed = 0

        for (boat in allBoats) {
            val isOwned = boat.ownerId == null || boat.ownerId == currentUserId

            if (isOwned) {
                kept++
            } else {
                database.boatDao().deleteBoat(boat)
                removed++
            }
        }

        return DataStats(kept, removed)
    }

    /**
     * Process trips: keep captain trips, mark crew trips read-only, delete others
     */
    private suspend fun processTrips(currentUserId: String?): TripStats {
        val allTrips = database.tripDao().getAllTripsSync()
        var kept = 0
        var markedReadOnly = 0
        var removed = 0

        // Get list of owned boats to determine if we should keep associated trips
        val ownedBoats = database.boatDao().getAllBoatsSync()
            .filter { it.ownerId == null || it.ownerId == currentUserId }
            .map { it.id }
            .toSet()

        for (trip in allTrips) {
            val isCaptain = trip.captainId == null || trip.captainId == currentUserId
            val isCrew = trip.role == "crew"
            val isOnOwnedBoat = trip.boatId in ownedBoats

            when {
                // Keep captain trips and observer trips on owned boats
                isCaptain && isOnOwnedBoat -> {
                    kept++
                }
                // Mark crew trips read-only if on owned boat
                isCrew && isOnOwnedBoat && !trip.isReadOnly -> {
                    val updatedTrip = trip.copy(isReadOnly = true)
                    database.tripDao().updateTrip(updatedTrip)
                    markedReadOnly++
                    kept++
                }
                // Already read-only crew trips on owned boats
                isCrew && isOnOwnedBoat && trip.isReadOnly -> {
                    kept++
                }
                // Delete trips on boats we don't own
                !isOnOwnedBoat -> {
                    database.tripDao().deleteTrip(trip)
                    removed++
                }
                // Delete non-captain, non-crew trips on owned boats
                else -> {
                    database.tripDao().deleteTrip(trip)
                    removed++
                }
            }
        }

        return TripStats(kept, markedReadOnly, removed)
    }

    /**
     * Process notes: keep locally created notes, delete server-synced notes from others
     */
    private suspend fun processNotes(currentUserId: String?): DataStats {
        val allNotes = database.noteDao().getAllNotesSync()
        var kept = 0
        var removed = 0

        // Get list of kept trips and boats to determine if we should keep associated notes
        val keptTrips = database.tripDao().getAllTripsSync().map { it.id }.toSet()
        val keptBoats = database.boatDao().getAllBoatsSync().map { it.id }.toSet()

        for (note in allNotes) {
            // Keep if:
            // - Not server-synced (originSource == null means locally created)
            // - Associated with a kept trip or boat
            val isLocallyCreated = note.originSource == null
            val hasValidTrip = note.tripId == null || note.tripId in keptTrips
            val hasValidBoat = note.boatId == null || note.boatId in keptBoats

            if (isLocallyCreated && hasValidTrip && hasValidBoat) {
                kept++
            } else {
                database.noteDao().deleteNote(note)
                removed++
            }
        }

        return DataStats(kept, removed)
    }

    /**
     * Process photos: delete orphaned photos not associated with kept trips/boats
     */
    private suspend fun processPhotos(): DataStats {
        val allPhotos = database.photoDao().getAllPhotosSync()
        var kept = 0
        var removed = 0

        val keptTrips = database.tripDao().getAllTripsSync().map { it.id }.toSet()
        val keptNotes = database.noteDao().getAllNotesSync().map { it.id }.toSet()
        val keptMaintenanceEvents = database.maintenanceEventDao().getAllEventsSync().map { it.id }.toSet()

        for (photo in allPhotos) {
            val isOrphaned = when (photo.entityType) {
                "trip" -> photo.entityId !in keptTrips
                "note" -> photo.entityId !in keptNotes
                "maintenance" -> photo.entityId !in keptMaintenanceEvents
                else -> true
            }

            if (isOrphaned) {
                database.photoDao().deletePhoto(photo)
                removed++
            } else {
                kept++
            }
        }

        return DataStats(kept, removed)
    }

    /**
     * Process maintenance templates and events: keep for owned boats, delete others
     */
    private suspend fun processMaintenanceData(): DataStats {
        val keptBoats = database.boatDao().getAllBoatsSync().map { it.id }.toSet()

        // Templates
        val allTemplates = database.maintenanceTemplateDao().getAllTemplatesSync()
        var templatesKept = 0
        var templatesRemoved = 0

        for (template in allTemplates) {
            if (template.boatId in keptBoats) {
                templatesKept++
            } else {
                database.maintenanceTemplateDao().deleteTemplate(template)
                templatesRemoved++
            }
        }

        // Events
        val keptTemplateIds = database.maintenanceTemplateDao().getAllTemplatesSync().map { it.id }.toSet()
        val allEvents = database.maintenanceEventDao().getAllEventsSync()
        var eventsKept = 0
        var eventsRemoved = 0

        for (event in allEvents) {
            if (event.templateId in keptTemplateIds) {
                eventsKept++
            } else {
                database.maintenanceEventDao().deleteEvent(event)
                eventsRemoved++
            }
        }

        return DataStats(templatesKept + eventsKept, templatesRemoved + eventsRemoved)
    }

    /**
     * Process todos: keep all (assumed user-created in current version)
     */
    private suspend fun processTodos(): DataStats {
        val todoLists = database.todoListDao().getAllTodoListsSync()
        return DataStats(kept = todoLists.size, removed = 0)
    }

    /**
     * Process marked locations: keep all (assumed user-created in current version)
     */
    private suspend fun processMarkedLocations(): DataStats {
        val locations = database.markedLocationDao().getAllMarkedLocationsSync()
        return DataStats(kept = locations.size, removed = 0)
    }

    /**
     * Clear server configuration and credentials
     */
    private fun clearServerConfiguration() {
        Log.d(TAG, "Clearing server configuration")
        securePreferences.remoteUrl = null
        securePreferences.jwtToken = null
        securePreferences.username = null
        // Keep localUrl and cert pins for potential reconnection
    }
}

/**
 * Statistics about data processing
 */
private data class DataStats(
    val kept: Int,
    val removed: Int = 0
)

/**
 * Statistics about trip processing
 */
private data class TripStats(
    val kept: Int,
    val markedReadOnly: Int,
    val removed: Int
)

/**
 * Result of disconnect operation
 */
data class DisconnectResult(
    val boatsKept: Int,
    val boatsRemoved: Int,
    val tripsKept: Int,
    val tripsMarkedReadOnly: Int,
    val tripsRemoved: Int,
    val notesKept: Int,
    val notesRemoved: Int,
    val photosKept: Int,
    val photosRemoved: Int,
    val todosKept: Int,
    val locationsKept: Int,
    val maintenanceKept: Int,
    val maintenanceRemoved: Int
) {
    val totalKept: Int
        get() = boatsKept + tripsKept + notesKept + photosKept + todosKept + locationsKept + maintenanceKept

    val totalRemoved: Int
        get() = boatsRemoved + tripsRemoved + notesRemoved + photosRemoved + maintenanceRemoved
}

/**
 * Exception thrown when disconnect fails
 */
class DisconnectException(message: String, cause: Throwable? = null) : Exception(message, cause)
