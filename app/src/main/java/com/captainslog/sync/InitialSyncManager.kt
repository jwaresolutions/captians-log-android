package com.captainslog.sync

import android.content.Context
import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.BoatDao
import com.captainslog.database.dao.NoteDao
import com.captainslog.database.dao.TripDao
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.NoteEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.network.models.CreateBoatRequest
import com.captainslog.network.models.CreateTripRequest
import com.captainslog.network.models.CreateNoteRequest
import com.captainslog.network.models.CreateGpsPointRequest
import com.captainslog.ui.sync.SyncConflict
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Manages initial sync when a user connects to a server for the first time.
 * Handles conflict detection and resolution when the same data exists locally and on server.
 */
class InitialSyncManager @Inject constructor(
    private val boatDao: BoatDao,
    private val tripDao: TripDao,
    private val noteDao: NoteDao,
    private val connectionManager: ConnectionManager,
    private val database: AppDatabase,
    private val context: Context
) {
    companion object {
        private const val TAG = "InitialSyncManager"

        @Volatile
        private var INSTANCE: InitialSyncManager? = null

        fun getInstance(context: Context, database: AppDatabase): InitialSyncManager {
            return INSTANCE ?: synchronized(this) {
                val connectionManager = ConnectionManager.getInstance(context)
                val instance = InitialSyncManager(
                    database.boatDao(),
                    database.tripDao(),
                    database.noteDao(),
                    connectionManager,
                    database,
                    context.applicationContext
                )
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Sealed class representing different stages of the initial sync process
     */
    sealed class SyncProgress {
        object Starting : SyncProgress()
        data class Uploading(val current: Int, val total: Int, val type: String) : SyncProgress()
        data class Downloading(val current: Int, val total: Int, val type: String) : SyncProgress()
        data class Conflicts(val conflicts: List<SyncConflict>) : SyncProgress()
        data class Merging(val current: Int, val total: Int) : SyncProgress()
        object Complete : SyncProgress()
        data class Error(val message: String) : SyncProgress()
    }

    /**
     * Data class for tracking sync conflicts
     */
    data class ConflictInfo(
        val entityType: String,
        val entityId: String,
        val conflictType: String,
        val localTimestamp: Date,
        val serverTimestamp: Date
    )

    /**
     * Performs initial synchronization with conflict detection and resolution
     */
    suspend fun performInitialSync(): Flow<SyncProgress> = flow {
        try {
            emit(SyncProgress.Starting)
            Log.d(TAG, "Starting initial sync...")

            // Step 1: Upload local data to server
            emit(SyncProgress.Uploading(0, 3, "boats"))
            val uploadedBoats = uploadLocalBoats()

            emit(SyncProgress.Uploading(1, 3, "trips"))
            val uploadedTrips = uploadLocalTrips()

            emit(SyncProgress.Uploading(2, 3, "notes"))
            val uploadedNotes = uploadLocalNotes()

            emit(SyncProgress.Uploading(3, 3, "complete"))
            Log.d(TAG, "Upload complete: $uploadedBoats boats, $uploadedTrips trips, $uploadedNotes notes")

            // Step 2: Download server data
            emit(SyncProgress.Downloading(0, 3, "boats"))
            val serverBoats = downloadServerBoats()

            emit(SyncProgress.Downloading(1, 3, "trips"))
            val serverTrips = downloadServerTrips()

            emit(SyncProgress.Downloading(2, 3, "notes"))
            val serverNotes = downloadServerNotes()

            emit(SyncProgress.Downloading(3, 3, "complete"))
            Log.d(TAG, "Download complete: ${serverBoats.size} boats, ${serverTrips.size} trips, ${serverNotes.size} notes")

            // Step 3: Detect conflicts (same ID, different content or timestamp)
            val conflicts = detectConflicts(serverBoats, serverTrips, serverNotes)

            if (conflicts.isNotEmpty()) {
                Log.d(TAG, "Detected ${conflicts.size} conflicts")
                emit(SyncProgress.Conflicts(conflicts))
                // Wait for conflict resolution - caller will handle this
                return@flow
            }

            // Step 4: Merge non-conflicting data
            emit(SyncProgress.Merging(0, serverBoats.size + serverTrips.size + serverNotes.size))
            mergeServerData(serverBoats, serverTrips, serverNotes)

            emit(SyncProgress.Complete)
            Log.d(TAG, "Initial sync completed successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error during initial sync", e)
            emit(SyncProgress.Error(e.message ?: "Unknown error during sync"))
        }
    }

    /**
     * Upload local boats that haven't been synced
     */
    private suspend fun uploadLocalBoats(): Int {
        val localBoats = boatDao.getAllBoatsSync().filter { !it.synced }
        Log.d(TAG, "Uploading ${localBoats.size} local boats")

        val apiService = connectionManager.getApiService()
        var uploadedCount = 0

        for (boat in localBoats) {
            try {
                val request = CreateBoatRequest(
                    name = boat.name,
                    metadata = mapOf(
                        "enabled" to boat.enabled,
                        "isActive" to boat.isActive,
                        "createdAt" to boat.createdAt.time
                    )
                )

                val response = apiService.createBoat(request)
                if (response.isSuccessful) {
                    // Mark as synced
                    boatDao.updateBoat(boat.copy(synced = true))
                    uploadedCount++
                    Log.d(TAG, "Uploaded boat: ${boat.name}")
                } else {
                    Log.w(TAG, "Failed to upload boat ${boat.name}: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading boat ${boat.name}", e)
            }
        }

        return uploadedCount
    }

    /**
     * Upload local trips that haven't been synced
     */
    private suspend fun uploadLocalTrips(): Int {
        val localTrips = tripDao.getAllTripsSync().filter { !it.synced }
        Log.d(TAG, "Uploading ${localTrips.size} local trips")

        val apiService = connectionManager.getApiService()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        var uploadedCount = 0

        for (trip in localTrips) {
            try {
                // Fetch GPS points for this trip
                val gpsPoints = database.gpsPointDao().getGpsPointsForTripSync(trip.id)

                val request = CreateTripRequest(
                    boatId = trip.boatId,
                    startTime = dateFormat.format(trip.startTime),
                    endTime = trip.endTime?.let { dateFormat.format(it) },
                    waterType = trip.waterType,
                    role = trip.role,
                    gpsPoints = gpsPoints.map { point ->
                        CreateGpsPointRequest(
                            latitude = point.latitude,
                            longitude = point.longitude,
                            altitude = point.altitude,
                            accuracy = point.accuracy,
                            speed = point.speed,
                            heading = point.heading,
                            timestamp = dateFormat.format(point.timestamp)
                        )
                    },
                    manualData = null // TODO: Add manual data if exists
                )

                val response = apiService.createTrip(request)
                if (response.isSuccessful) {
                    tripDao.updateTrip(trip.copy(synced = true))
                    uploadedCount++
                    Log.d(TAG, "Uploaded trip: ${trip.id}")
                } else {
                    Log.w(TAG, "Failed to upload trip ${trip.id}: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading trip ${trip.id}", e)
            }
        }

        return uploadedCount
    }

    /**
     * Upload local notes that haven't been synced
     */
    private suspend fun uploadLocalNotes(): Int {
        val localNotes = noteDao.getAllNotesSync().filter { !it.synced }
        Log.d(TAG, "Uploading ${localNotes.size} local notes")

        val apiService = connectionManager.getApiService()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        var uploadedCount = 0

        for (note in localNotes) {
            try {
                val request = CreateNoteRequest(
                    type = note.type,
                    content = note.content,
                    tags = note.tags,
                    tripId = note.tripId,
                    boatId = note.boatId
                )

                val response = apiService.createNote(request)
                if (response.isSuccessful) {
                    noteDao.updateNote(note.copy(synced = true))
                    uploadedCount++
                    Log.d(TAG, "Uploaded note: ${note.id}")
                } else {
                    Log.w(TAG, "Failed to upload note ${note.id}: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading note ${note.id}", e)
            }
        }

        return uploadedCount
    }

    /**
     * Download all boats from server
     */
    private suspend fun downloadServerBoats(): List<BoatEntity> {
        val apiService = connectionManager.getApiService()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        try {
            val response = apiService.getBoats()
            if (response.isSuccessful && response.body() != null) {
                val boats = response.body()!!.data.map { boat ->
                    BoatEntity(
                        id = boat.id,
                        name = boat.name,
                        enabled = boat.enabled,
                        isActive = boat.isActive,
                        synced = true,
                        lastModified = dateFormat.parse(boat.updatedAt) ?: Date(),
                        createdAt = dateFormat.parse(boat.createdAt) ?: Date()
                    )
                }
                Log.d(TAG, "Downloaded ${boats.size} boats from server")
                return boats
            } else {
                Log.w(TAG, "Failed to download boats: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading boats", e)
        }

        return emptyList()
    }

    /**
     * Download all trips from server
     */
    private suspend fun downloadServerTrips(): List<TripEntity> {
        val apiService = connectionManager.getApiService()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        try {
            val response = apiService.getTrips()
            if (response.isSuccessful && response.body() != null) {
                val trips = response.body()!!.data.map { trip ->
                    TripEntity(
                        id = trip.id,
                        boatId = trip.boatId,
                        startTime = dateFormat.parse(trip.startTime) ?: Date(),
                        endTime = trip.endTime?.let { dateFormat.parse(it) },
                        waterType = trip.waterType,
                        role = trip.role,
                        synced = true,
                        lastModified = dateFormat.parse(trip.updatedAt) ?: Date()
                    )
                }
                Log.d(TAG, "Downloaded ${trips.size} trips from server")
                return trips
            } else {
                Log.w(TAG, "Failed to download trips: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading trips", e)
        }

        return emptyList()
    }

    /**
     * Download all notes from server
     */
    private suspend fun downloadServerNotes(): List<NoteEntity> {
        val apiService = connectionManager.getApiService()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        try {
            val response = apiService.getNotes()
            if (response.isSuccessful && response.body() != null) {
                val notes = response.body()!!.data.map { note ->
                    NoteEntity(
                        id = note.id,
                        type = note.type,
                        content = note.content,
                        tags = note.tags,
                        tripId = note.tripId,
                        boatId = note.boatId,
                        synced = true,
                        lastModified = dateFormat.parse(note.updatedAt) ?: Date(),
                        createdAt = dateFormat.parse(note.createdAt) ?: Date()
                    )
                }
                Log.d(TAG, "Downloaded ${notes.size} notes from server")
                return notes
            } else {
                Log.w(TAG, "Failed to download notes: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading notes", e)
        }

        return emptyList()
    }

    /**
     * Detect conflicts between local and server data
     * A conflict occurs when the same ID exists both locally and on server
     * with different timestamps or content
     */
    private suspend fun detectConflicts(
        serverBoats: List<BoatEntity>,
        serverTrips: List<TripEntity>,
        serverNotes: List<NoteEntity>
    ): List<SyncConflict> {
        val conflicts = mutableListOf<SyncConflict>()

        // Check boat conflicts
        val localBoats = boatDao.getAllBoatsSync()
        for (serverBoat in serverBoats) {
            val localBoat = localBoats.find { it.id == serverBoat.id }
            if (localBoat != null && hasBoatConflict(localBoat, serverBoat)) {
                conflicts.add(
                    SyncConflict.BoatConflict(
                        entityId = localBoat.id,
                        local = localBoat,
                        server = serverBoat,
                        localTimestamp = localBoat.lastModified,
                        serverTimestamp = serverBoat.lastModified
                    )
                )
                Log.d(TAG, "Boat conflict detected: ${localBoat.name}")
            }
        }

        // Check trip conflicts
        val localTrips = tripDao.getAllTripsSync()
        for (serverTrip in serverTrips) {
            val localTrip = localTrips.find { it.id == serverTrip.id }
            if (localTrip != null && hasTripConflict(localTrip, serverTrip)) {
                conflicts.add(
                    SyncConflict.TripConflict(
                        entityId = localTrip.id,
                        local = localTrip,
                        server = serverTrip,
                        localTimestamp = localTrip.lastModified,
                        serverTimestamp = serverTrip.lastModified
                    )
                )
                Log.d(TAG, "Trip conflict detected: ${localTrip.id}")
            }
        }

        // Check note conflicts
        val localNotes = noteDao.getAllNotesSync()
        for (serverNote in serverNotes) {
            val localNote = localNotes.find { it.id == serverNote.id }
            if (localNote != null && hasNoteConflict(localNote, serverNote)) {
                conflicts.add(
                    SyncConflict.NoteConflict(
                        entityId = localNote.id,
                        local = localNote,
                        server = serverNote,
                        localTimestamp = localNote.lastModified,
                        serverTimestamp = serverNote.lastModified
                    )
                )
                Log.d(TAG, "Note conflict detected: ${localNote.id}")
            }
        }

        return conflicts
    }

    /**
     * Check if two boat entities have a conflict
     */
    private fun hasBoatConflict(local: BoatEntity, server: BoatEntity): Boolean {
        return local.name != server.name ||
                local.enabled != server.enabled ||
                local.isActive != server.isActive ||
                Math.abs(local.lastModified.time - server.lastModified.time) > 1000 // 1 second tolerance
    }

    /**
     * Check if two trip entities have a conflict
     */
    private fun hasTripConflict(local: TripEntity, server: TripEntity): Boolean {
        return local.waterType != server.waterType ||
                local.role != server.role ||
                Math.abs(local.startTime.time - server.startTime.time) > 1000 ||
                (local.endTime?.time ?: 0) != (server.endTime?.time ?: 0) ||
                Math.abs(local.lastModified.time - server.lastModified.time) > 1000
    }

    /**
     * Check if two note entities have a conflict
     */
    private fun hasNoteConflict(local: NoteEntity, server: NoteEntity): Boolean {
        return local.type != server.type ||
                local.content != server.content ||
                local.tags != server.tags ||
                Math.abs(local.lastModified.time - server.lastModified.time) > 1000
    }

    /**
     * Merge non-conflicting server data into local database
     */
    private suspend fun mergeServerData(
        serverBoats: List<BoatEntity>,
        serverTrips: List<TripEntity>,
        serverNotes: List<NoteEntity>
    ) {
        val localBoats = boatDao.getAllBoatsSync()
        val localTrips = tripDao.getAllTripsSync()
        val localNotes = noteDao.getAllNotesSync()

        // Insert or update boats that don't conflict
        for (serverBoat in serverBoats) {
            if (localBoats.none { it.id == serverBoat.id }) {
                boatDao.insertBoat(serverBoat)
                Log.d(TAG, "Merged server boat: ${serverBoat.name}")
            }
        }

        // Insert or update trips that don't conflict
        for (serverTrip in serverTrips) {
            if (localTrips.none { it.id == serverTrip.id }) {
                tripDao.insertTrip(serverTrip)
                Log.d(TAG, "Merged server trip: ${serverTrip.id}")
            }
        }

        // Insert or update notes that don't conflict
        for (serverNote in serverNotes) {
            if (localNotes.none { it.id == serverNote.id }) {
                noteDao.insertNote(serverNote)
                Log.d(TAG, "Merged server note: ${serverNote.id}")
            }
        }
    }

    /**
     * Resolve a conflict by choosing local or server version
     */
    suspend fun resolveConflict(conflict: SyncConflict, useLocal: Boolean) {
        when (conflict) {
            is SyncConflict.BoatConflict -> {
                if (useLocal) {
                    // Upload only the conflicting local boat to server
                    uploadConflictingBoat(conflict.local)
                } else {
                    // Replace local with server version
                    boatDao.updateBoat(conflict.server)
                }
                Log.d(TAG, "Resolved boat conflict for ${conflict.entityId}, useLocal=$useLocal")
            }
            is SyncConflict.TripConflict -> {
                if (useLocal) {
                    // Upload only the conflicting local trip to server
                    uploadConflictingTrip(conflict.local)
                } else {
                    tripDao.updateTrip(conflict.server)
                }
                Log.d(TAG, "Resolved trip conflict for ${conflict.entityId}, useLocal=$useLocal")
            }
            is SyncConflict.NoteConflict -> {
                if (useLocal) {
                    // Upload only the conflicting local note to server
                    uploadConflictingNote(conflict.local)
                } else {
                    noteDao.updateNote(conflict.server)
                }
                Log.d(TAG, "Resolved note conflict for ${conflict.entityId}, useLocal=$useLocal")
            }
        }
    }

    /**
     * Upload a specific boat to resolve a conflict
     */
    private suspend fun uploadConflictingBoat(boat: BoatEntity) {
        val apiService = connectionManager.getApiService()
        try {
            val request = CreateBoatRequest(
                name = boat.name,
                metadata = mapOf(
                    "enabled" to boat.enabled,
                    "isActive" to boat.isActive,
                    "createdAt" to boat.createdAt.time
                )
            )

            val response = apiService.createBoat(request)
            if (response.isSuccessful) {
                boatDao.updateBoat(boat.copy(synced = true))
                Log.d(TAG, "Uploaded conflicting boat: ${boat.name}")
            } else {
                Log.w(TAG, "Failed to upload conflicting boat ${boat.name}: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading conflicting boat ${boat.name}", e)
        }
    }

    /**
     * Upload a specific trip to resolve a conflict
     */
    private suspend fun uploadConflictingTrip(trip: TripEntity) {
        val apiService = connectionManager.getApiService()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        try {
            val gpsPoints = database.gpsPointDao().getGpsPointsForTripSync(trip.id)

            val request = CreateTripRequest(
                boatId = trip.boatId,
                startTime = dateFormat.format(trip.startTime),
                endTime = trip.endTime?.let { dateFormat.format(it) },
                waterType = trip.waterType,
                role = trip.role,
                gpsPoints = gpsPoints.map { point ->
                    CreateGpsPointRequest(
                        latitude = point.latitude,
                        longitude = point.longitude,
                        altitude = point.altitude,
                        accuracy = point.accuracy,
                        speed = point.speed,
                        heading = point.heading,
                        timestamp = dateFormat.format(point.timestamp)
                    )
                },
                manualData = null
            )

            val response = apiService.createTrip(request)
            if (response.isSuccessful) {
                tripDao.updateTrip(trip.copy(synced = true))
                Log.d(TAG, "Uploaded conflicting trip: ${trip.id}")
            } else {
                Log.w(TAG, "Failed to upload conflicting trip ${trip.id}: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading conflicting trip ${trip.id}", e)
        }
    }

    /**
     * Upload a specific note to resolve a conflict
     */
    private suspend fun uploadConflictingNote(note: NoteEntity) {
        val apiService = connectionManager.getApiService()

        try {
            val request = CreateNoteRequest(
                type = note.type,
                content = note.content,
                tags = note.tags,
                tripId = note.tripId,
                boatId = note.boatId
            )

            val response = apiService.createNote(request)
            if (response.isSuccessful) {
                noteDao.updateNote(note.copy(synced = true))
                Log.d(TAG, "Uploaded conflicting note: ${note.id}")
            } else {
                Log.w(TAG, "Failed to upload conflicting note ${note.id}: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading conflicting note ${note.id}", e)
        }
    }
}
