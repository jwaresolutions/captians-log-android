package com.captainslog.qr

import com.captainslog.database.dao.BoatDao
import com.captainslog.database.dao.ImportedQrDao
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.ImportedQrEntity
import com.google.gson.JsonElement
import java.util.Date
import java.util.UUID

/**
 * Handles importing boat data from QR codes into the database.
 *
 * Provides parsing, duplicate detection, and import operations for boat QR codes.
 */
class QrBoatImporter(
    private val boatDao: BoatDao,
    private val importedQrDao: ImportedQrDao
) {
    /**
     * Parse JSON data from QR code into a BoatEntity.
     *
     * @param data JsonElement containing boat data from QR
     * @return BoatEntity with all fields populated from JSON
     */
    fun parseBoatData(data: JsonElement): BoatEntity {
        val json = data.asJsonObject

        return BoatEntity(
            id = UUID.randomUUID().toString(),
            name = json.get("name")?.asString ?: "",
            enabled = true,
            isActive = false,
            synced = false,
            lastModified = Date(),
            createdAt = Date(),
            // Vessel details
            officialNumber = json.get("officialNumber")?.asString,
            grossTons = json.get("grossTons")?.asDouble,
            lengthFeet = json.get("lengthFeet")?.asInt,
            lengthInches = json.get("lengthInches")?.asInt,
            widthFeet = json.get("widthFeet")?.asInt,
            widthInches = json.get("widthInches")?.asInt,
            depthFeet = json.get("depthFeet")?.asInt,
            depthInches = json.get("depthInches")?.asInt,
            propulsionType = json.get("propulsionType")?.asString,
            // Owner/Operator info
            ownerFirstName = json.get("ownerFirstName")?.asString,
            ownerMiddleName = json.get("ownerMiddleName")?.asString,
            ownerLastName = json.get("ownerLastName")?.asString,
            ownerStreetAddress = json.get("ownerStreetAddress")?.asString,
            ownerCity = json.get("ownerCity")?.asString,
            ownerState = json.get("ownerState")?.asString,
            ownerZipCode = json.get("ownerZipCode")?.asString,
            ownerEmail = json.get("ownerEmail")?.asString,
            ownerPhone = json.get("ownerPhone")?.asString
        )
    }

    /**
     * Find an existing boat that might be a duplicate of the QR data.
     *
     * Searches by:
     * 1. Official number (if provided) - exact match
     * 2. Boat name - case-insensitive exact match
     *
     * @param name Boat name from QR
     * @param officialNumber Optional official number from QR
     * @return Existing BoatEntity if duplicate found, null otherwise
     */
    suspend fun findDuplicate(name: String, officialNumber: String?): BoatEntity? {
        val allBoats = boatDao.getAllBoatsSync()

        // First check by official number (most reliable)
        if (!officialNumber.isNullOrBlank()) {
            val byOfficialNumber = allBoats.find {
                it.officialNumber?.equals(officialNumber, ignoreCase = true) == true
            }
            if (byOfficialNumber != null) return byOfficialNumber
        }

        // Then check by name (case-insensitive)
        return allBoats.find {
            it.name.equals(name, ignoreCase = true)
        }
    }

    /**
     * Import boat data as a new boat entity.
     *
     * Creates a new boat record and tracks the QR import.
     *
     * @param boatData JsonElement containing boat data
     * @param qrId QR envelope ID for tracking
     * @return ID of the newly created boat
     */
    suspend fun importAsNew(boatData: JsonElement, qrId: String): String {
        val boat = parseBoatData(boatData)

        // Insert boat
        boatDao.insertBoat(boat)

        // Track QR import
        importedQrDao.insert(
            ImportedQrEntity(
                qrId = qrId,
                type = "boat",
                importedAt = Date(),
                tripCount = 0
            )
        )

        return boat.id
    }

    /**
     * Update an existing boat with QR data.
     *
     * Merges QR data into existing boat, preserving the original ID and metadata.
     *
     * @param existingId ID of the existing boat to update
     * @param boatData JsonElement containing boat data
     * @param qrId QR envelope ID for tracking
     * @return ID of the updated boat (same as existingId)
     */
    suspend fun updateExisting(existingId: String, boatData: JsonElement, qrId: String): String {
        val existing = boatDao.getBoatById(existingId)
            ?: throw IllegalArgumentException("Boat with ID $existingId not found")

        val parsed = parseBoatData(boatData)

        // Update existing boat with new data, preserving ID and metadata
        val updated = existing.copy(
            name = parsed.name,
            lastModified = Date(),
            // Update vessel details
            officialNumber = parsed.officialNumber,
            grossTons = parsed.grossTons,
            lengthFeet = parsed.lengthFeet,
            lengthInches = parsed.lengthInches,
            widthFeet = parsed.widthFeet,
            widthInches = parsed.widthInches,
            depthFeet = parsed.depthFeet,
            depthInches = parsed.depthInches,
            propulsionType = parsed.propulsionType,
            // Update owner info
            ownerFirstName = parsed.ownerFirstName,
            ownerMiddleName = parsed.ownerMiddleName,
            ownerLastName = parsed.ownerLastName,
            ownerStreetAddress = parsed.ownerStreetAddress,
            ownerCity = parsed.ownerCity,
            ownerState = parsed.ownerState,
            ownerZipCode = parsed.ownerZipCode,
            ownerEmail = parsed.ownerEmail,
            ownerPhone = parsed.ownerPhone
        )

        boatDao.updateBoat(updated)

        // Track QR import
        importedQrDao.insert(
            ImportedQrEntity(
                qrId = qrId,
                type = "boat",
                importedAt = Date(),
                tripCount = 0
            )
        )

        return existingId
    }
}
