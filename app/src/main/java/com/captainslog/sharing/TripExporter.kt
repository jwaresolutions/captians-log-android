package com.captainslog.sharing

import android.content.Context
import com.captainslog.database.dao.TripDao
import com.captainslog.database.dao.BoatDao
import com.captainslog.database.dao.GpsPointDao
import com.captainslog.database.dao.NoteDao
import com.captainslog.database.dao.PhotoDao
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.models.*
import com.google.gson.Gson
import java.io.File

class TripExporter(
    private val context: Context,
    private val tripDao: TripDao,
    private val boatDao: BoatDao,
    private val gpsPointDao: GpsPointDao,
    private val noteDao: NoteDao,
    private val photoDao: PhotoDao,
    private val securePreferences: SecurePreferences
) {
    /**
     * Export a trip and all related data to a .captainslog file
     * @param tripId The trip to export
     * @return The exported file
     */
    suspend fun exportTrip(tripId: String): File {
        val trip = tripDao.getTripById(tripId)
            ?: throw IllegalArgumentException("Trip not found: $tripId")

        val boat = boatDao.getBoatById(trip.boatId)
        val gpsPoints = gpsPointDao.getGpsPointsForTripSync(tripId)
        val notes = noteDao.getNotesByTripSync(tripId)
        val photos = photoDao.getPhotosForEntitySync("trip", tripId)

        val exportData = TripExportData(
            origin = "device:${securePreferences.deviceId}",
            exportedAt = System.currentTimeMillis(),
            trip = mapToTripData(trip),
            boat = boat?.let { mapToBoatData(it) },
            gpsPoints = gpsPoints.map { mapToGpsPointData(it) },
            notes = notes.map { mapToNoteData(it) },
            photos = photos.map { mapToPhotoData(it) }
        )

        val json = Gson().toJson(exportData)
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, "trip_${tripId}_${System.currentTimeMillis()}.captainslog")
        file.writeText(json)
        return file
    }

    private fun mapToTripData(trip: com.captainslog.database.entities.TripEntity): TripData {
        return TripData(
            id = trip.id,
            boatId = trip.boatId,
            startTime = trip.startTime.time,
            endTime = trip.endTime?.time,
            waterType = trip.waterType,
            role = trip.role,
            weatherConditions = trip.weatherConditions,
            destination = trip.destination,
            passengerCount = trip.numberOfPassengers,
            fuelConsumed = trip.fuelConsumed
        )
    }

    private fun mapToBoatData(boat: com.captainslog.database.entities.BoatEntity): BoatData {
        return BoatData(
            id = boat.id,
            name = boat.name,
            enabled = boat.enabled,
            boatType = null,
            registrationNumber = null,
            fuelCapacity = null,
            engineHours = null
        )
    }

    private fun mapToGpsPointData(point: com.captainslog.database.entities.GpsPointEntity): GpsPointData {
        return GpsPointData(
            lat = point.latitude,
            lng = point.longitude,
            ts = point.timestamp.time,
            speed = point.speed,
            heading = point.heading
        )
    }

    private fun mapToNoteData(note: com.captainslog.database.entities.NoteEntity): NoteData {
        return NoteData(
            id = note.id,
            content = note.content,
            createdAt = note.createdAt.time,
            noteType = note.type
        )
    }

    private fun mapToPhotoData(photo: com.captainslog.database.entities.PhotoEntity): PhotoData {
        return PhotoData(
            id = photo.id,
            filename = File(photo.localPath).name,
            caption = null,
            base64 = null  // Don't include photo data in export for now
        )
    }
}
