package com.captainslog.qr

import com.captainslog.database.dao.ImportedQrDao
import com.captainslog.database.dao.TripDao
import com.captainslog.database.entities.ImportedQrEntity
import com.captainslog.database.entities.TripEntity
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/**
 * Handles importing trip data from QR code payloads into the local database.
 *
 * Responsibilities:
 * - Parse JSON trip data from QR payloads
 * - Detect overlapping trips for a given boat
 * - Import trips while recording the QR import for deduplication
 */
class QrTripImporter(
    private val tripDao: TripDao,
    private val importedQrDao: ImportedQrDao
) {
    /**
     * Represents a single trip as encoded in the QR payload JSON.
     * Field names match the web generator output.
     */
    data class TripImportData(
        @SerializedName("startDateGmt") val startDateGmt: String,
        @SerializedName("endDateGmt") val endDateGmt: String,
        @SerializedName("startTzOffset") val startTzOffset: String,
        @SerializedName("endTzOffset") val endTzOffset: String,
        @SerializedName("departurePort") val departurePort: String,
        @SerializedName("arrivalPort") val arrivalPort: String,
        @SerializedName("waterType") val waterType: String,
        @SerializedName("bodyOfWater") val bodyOfWater: String,
        @SerializedName("crewRole") val crewRole: String?,
        @SerializedName("masterName") val masterName: String?
    )

    private val gson = Gson()
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    data class ParseResult(
        val trips: List<TripImportData>,
        val errors: List<String>
    )

    /**
     * Parse a JsonElement (expected JSON array) into a list of TripImportData.
     * Validates that each trip's arrival time is not before its departure time.
     *
     * @param data JsonElement from QrProtocol decode (should be a JsonArray)
     * @return ParseResult with valid trips and any validation errors
     * @throws IllegalArgumentException if data is not a JSON array
     */
    fun parseTripData(data: JsonElement): ParseResult {
        require(data.isJsonArray) { "Expected JSON array of trips" }
        val trips = mutableListOf<TripImportData>()
        val errors = mutableListOf<String>()

        data.asJsonArray.forEachIndexed { index, element ->
            val trip = gson.fromJson(element, TripImportData::class.java)
            val startDate = parseGmtDate(trip.startDateGmt)
            val endDate = parseGmtDate(trip.endDateGmt)

            if (startDate != null && endDate != null && endDate.before(startDate)) {
                errors.add("Trip ${index + 1}: Arrival time is before departure time")
            } else {
                trips.add(trip)
            }
        }

        return ParseResult(trips, errors)
    }

    /**
     * Check for existing trips that overlap with the imported trips for a given boat.
     *
     * @param boatId The boat to check overlaps against
     * @param trips List of trips to check
     * @return Map of trip index to list of conflicting existing TripEntity objects.
     *         Only indices with conflicts are included.
     */
    suspend fun findOverlaps(
        boatId: String,
        trips: List<TripImportData>
    ): Map<Int, List<TripEntity>> {
        val overlaps = mutableMapOf<Int, List<TripEntity>>()
        trips.forEachIndexed { index, trip ->
            val startDate = parseGmtDate(trip.startDateGmt)
            val endDate = parseGmtDate(trip.endDateGmt)
            if (startDate != null && endDate != null) {
                val conflicts = tripDao.getOverlappingTrips(boatId, startDate, endDate)
                if (conflicts.isNotEmpty()) {
                    overlaps[index] = conflicts
                }
            }
        }
        return overlaps
    }

    /**
     * Import trips into the database, skipping any indices in the skip set.
     * Records the QR import for duplicate detection.
     *
     * @param boatId Boat to associate trips with
     * @param trips Full list of parsed trip data
     * @param skipIndices Indices of trips to skip (e.g., due to conflicts)
     * @param qrId QR envelope ID for dedup tracking
     * @return Number of trips actually imported
     */
    suspend fun importTrips(
        boatId: String,
        trips: List<TripImportData>,
        skipIndices: Set<Int>,
        qrId: String
    ): Int {
        val entitiesToImport = trips.mapIndexedNotNull { index, trip ->
            if (index in skipIndices) return@mapIndexedNotNull null
            tripDataToEntity(boatId, trip)
        }

        if (entitiesToImport.isNotEmpty()) {
            tripDao.insertTrips(entitiesToImport)
        }

        // Record QR import for deduplication
        importedQrDao.insert(
            ImportedQrEntity(
                qrId = qrId,
                type = "trip",
                importedAt = Date()
            )
        )

        return entitiesToImport.size
    }

    /**
     * Convert a single TripImportData to a TripEntity.
     */
    private fun tripDataToEntity(boatId: String, trip: TripImportData): TripEntity? {
        val startDate = parseGmtDate(trip.startDateGmt) ?: return null
        val endDate = parseGmtDate(trip.endDateGmt)

        return TripEntity(
            id = UUID.randomUUID().toString(),
            boatId = boatId,
            startTime = startDate,
            endTime = endDate,
            waterType = trip.waterType,
            role = if (!trip.crewRole.isNullOrBlank()) trip.crewRole else "master",
            destination = trip.departurePort + " → " + trip.arrivalPort,
            captainName = trip.masterName,
            bodyOfWater = trip.bodyOfWater,
            synced = false,
            lastModified = Date(),
            createdAt = Date()
        )
    }

    /**
     * Parse an ISO 8601 GMT date string to java.util.Date.
     */
    private fun parseGmtDate(dateStr: String): Date? {
        return try {
            isoFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
}
