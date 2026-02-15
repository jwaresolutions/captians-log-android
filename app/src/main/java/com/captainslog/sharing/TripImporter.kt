package com.captainslog.sharing

import com.captainslog.database.dao.TripDao
import com.captainslog.database.dao.BoatDao
import com.captainslog.database.dao.GpsPointDao
import com.captainslog.database.dao.NoteDao
import com.captainslog.database.dao.PhotoDao
import com.captainslog.database.entities.*
import com.captainslog.sharing.models.*
import com.google.gson.Gson
import java.io.File
import java.util.Date

/**
 * Result of importing a boat or trip from P2P sharing.
 */
sealed class ImportResult {
    data class Created(val id: String) : ImportResult()
    data class Updated(val id: String) : ImportResult()
    data class Skipped(val reason: String) : ImportResult()
    data class Error(val message: String) : ImportResult()
}

class TripImporter(
    private val tripDao: TripDao,
    private val boatDao: BoatDao,
    private val gpsPointDao: GpsPointDao,
    private val noteDao: NoteDao,
    private val photoDao: PhotoDao
) {
    /**
     * Import a trip from a .captainslog file
     * @param file The .captainslog file to import
     * @return ImportResult indicating success, update, skip, or error
     */
    suspend fun importTrip(file: File): ImportResult {
        return try {
            val json = file.readText()
            val exportData = Gson().fromJson(json, TripExportData::class.java)

            // Validate version
            if (exportData.version != 1) {
                return ImportResult.Error("Unsupported file version: ${exportData.version}")
            }

            // Check for existing trip with same origin + id
            val existing = tripDao.getTripByOrigin(exportData.origin, exportData.trip.id)

            if (existing != null && exportData.exportedAt <= (existing.originTimestamp ?: 0)) {
                return ImportResult.Skipped("Local version is newer or same age")
            }

            // Import or update boat if included
            exportData.boat?.let {
                importBoat(it, exportData.origin, exportData.exportedAt)
            }

            // Import trip with all related data
            val tripEntity = mapToTripEntity(exportData)
            tripDao.insertTrip(tripEntity)

            // Import GPS points
            val gpsPointEntities = exportData.gpsPoints.map { point ->
                GpsPointEntity(
                    tripId = exportData.trip.id,
                    latitude = point.lat,
                    longitude = point.lng,
                    timestamp = Date(point.ts),
                    speed = point.speed,
                    heading = point.heading
                )
            }
            gpsPointDao.insertGpsPoints(gpsPointEntities)

            // Import notes
            val noteEntities = exportData.notes.map { note ->
                NoteEntity(
                    id = note.id,
                    content = note.content,
                    type = note.noteType ?: "trip",
                    tripId = exportData.trip.id,
                    createdAt = Date(note.createdAt),
                    lastModified = Date(note.createdAt),
                    originSource = exportData.origin,
                    originTimestamp = exportData.exportedAt,
                    synced = false
                )
            }
            noteDao.insertNotes(noteEntities)

            // Photos are not imported (base64 data not included in export)
            // In a full implementation, photos would need to be handled separately

            if (existing != null) {
                ImportResult.Updated(id = tripEntity.id)
            } else {
                ImportResult.Created(id = tripEntity.id)
            }
        } catch (e: Exception) {
            ImportResult.Error("Import failed: ${e.message}")
        }
    }

    private suspend fun importBoat(boatData: BoatData, origin: String, timestamp: Long) {
        val existing = boatDao.getBoatByOrigin(origin, boatData.id)

        // Only import/update boat if this version is newer or it doesn't exist
        if (existing == null || timestamp > (existing.originTimestamp ?: 0)) {
            val boatEntity = BoatEntity(
                id = boatData.id,
                name = boatData.name,
                enabled = boatData.enabled,
                isActive = false,  // Don't auto-activate imported boats
                synced = false,
                originSource = origin,
                originTimestamp = timestamp
            )
            boatDao.insertBoat(boatEntity)
        }
    }

    private fun mapToTripEntity(exportData: TripExportData): TripEntity {
        val trip = exportData.trip
        return TripEntity(
            id = trip.id,
            boatId = trip.boatId,
            startTime = Date(trip.startTime),
            endTime = trip.endTime?.let { Date(it) },
            waterType = trip.waterType ?: "inland",
            role = trip.role ?: "master",
            weatherConditions = trip.weatherConditions,
            destination = trip.destination,
            numberOfPassengers = trip.passengerCount,
            fuelConsumed = trip.fuelConsumed,
            synced = false,
            originSource = exportData.origin,
            originTimestamp = exportData.exportedAt,
            isReadOnly = true  // Imported trips are read-only by default
        )
    }
}
