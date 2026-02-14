package com.captainslog.backup

import androidx.room.withTransaction
import com.captainslog.BuildConfig
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object BackupManager {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    data class BackupData(
        val backupVersion: Int,
        val exportedAt: String,
        val appVersion: String,
        val boats: List<BoatEntity>,
        val trips: List<TripEntity>,
        val notes: List<NoteEntity>,
        val photos: List<PhotoEntity>,
        val gpsPoints: List<GpsPointEntity>,
        val crewMembers: List<CrewMemberEntity>,
        val todoLists: List<TodoListEntity>,
        val todoItems: List<TodoItemEntity>,
        val maintenanceTemplates: List<MaintenanceTemplateEntity>,
        val maintenanceEvents: List<MaintenanceEventEntity>,
        val markedLocations: List<MarkedLocationEntity>
    )

    data class ImportStats(
        val boats: Int,
        val trips: Int,
        val notes: Int,
        val photos: Int,
        val gpsPoints: Int,
        val crewMembers: Int,
        val todoLists: Int,
        val todoItems: Int,
        val maintenanceTemplates: Int,
        val maintenanceEvents: Int,
        val markedLocations: Int
    )

    suspend fun exportToJson(database: AppDatabase): String {
        val exportedAt = DateTimeFormatter.ISO_INSTANT
            .format(Instant.now().atOffset(ZoneOffset.UTC))

        val backupData = BackupData(
            backupVersion = 1,
            exportedAt = exportedAt,
            appVersion = BuildConfig.VERSION_NAME,
            boats = database.boatDao().getAllBoatsSync(),
            trips = database.tripDao().getAllTripsSync(),
            notes = database.noteDao().getAllNotesSync(),
            photos = database.photoDao().getAllPhotosSync(),
            gpsPoints = database.gpsPointDao().getAllGpsPointsSync(),
            crewMembers = database.crewMemberDao().getAllCrewMembersSync(),
            todoLists = database.todoListDao().getAllTodoListsSync(),
            todoItems = database.todoItemDao().getAllTodoItemsSync(),
            maintenanceTemplates = database.maintenanceTemplateDao().getAllTemplatesSync(),
            maintenanceEvents = database.maintenanceEventDao().getAllEventsSync(),
            markedLocations = database.markedLocationDao().getAllMarkedLocationsSync()
        )

        return gson.toJson(backupData)
    }

    suspend fun importFromJson(database: AppDatabase, json: String): ImportStats {
        val backupData = gson.fromJson(json, BackupData::class.java)

        database.withTransaction {
            // Import in order respecting foreign key dependencies
            // 1. Boats (no dependencies)
            database.boatDao().insertBoats(backupData.boats)

            // 2. Trips (depends on boats)
            database.tripDao().insertTrips(backupData.trips)

            // 3. Child entities (depend on boats/trips)
            database.crewMemberDao().insertCrewMembers(backupData.crewMembers)
            database.gpsPointDao().insertGpsPoints(backupData.gpsPoints)
            database.noteDao().insertNotes(backupData.notes)

            // Photos need individual inserts
            backupData.photos.forEach { photo ->
                database.photoDao().insertPhoto(photo)
            }

            // 4. Todo system (depends on trips/boats)
            database.todoListDao().insertTodoLists(backupData.todoLists)
            database.todoItemDao().insertTodoItems(backupData.todoItems)

            // 5. Maintenance system (depends on boats)
            database.maintenanceTemplateDao().insertTemplates(backupData.maintenanceTemplates)
            database.maintenanceEventDao().insertEvents(backupData.maintenanceEvents)

            // 6. Marked locations (depends on trips)
            database.markedLocationDao().insertMarkedLocations(backupData.markedLocations)
        }

        return ImportStats(
            boats = backupData.boats.size,
            trips = backupData.trips.size,
            notes = backupData.notes.size,
            photos = backupData.photos.size,
            gpsPoints = backupData.gpsPoints.size,
            crewMembers = backupData.crewMembers.size,
            todoLists = backupData.todoLists.size,
            todoItems = backupData.todoItems.size,
            maintenanceTemplates = backupData.maintenanceTemplates.size,
            maintenanceEvents = backupData.maintenanceEvents.size,
            markedLocations = backupData.markedLocations.size
        )
    }
}
