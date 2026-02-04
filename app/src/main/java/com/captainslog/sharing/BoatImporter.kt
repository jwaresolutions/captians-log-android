package com.captainslog.sharing

import com.captainslog.database.dao.BoatDao
import com.captainslog.database.entities.BoatEntity
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.models.BoatShareData
import java.util.Date

/**
 * Handles importing boats from shared QR codes.
 */
class BoatImporter(
    private val boatDao: BoatDao,
    private val securePreferences: SecurePreferences
) {
    /**
     * Imports a boat from shared data, handling duplicates and updates.
     *
     * @param shareData The boat data from the scanned QR code
     * @return ImportResult indicating what happened
     */
    suspend fun importBoat(shareData: BoatShareData): ImportResult {
        // Check for existing boat with same origin + id
        val existing = boatDao.getBoatByOrigin(shareData.origin, shareData.data.id)

        if (existing != null) {
            // Update if incoming is newer
            if (shareData.timestamp > (existing.originTimestamp ?: 0)) {
                val updatedBoat = existing.copy(
                    name = shareData.data.name,
                    enabled = shareData.data.enabled,
                    originTimestamp = shareData.timestamp,
                    lastModified = Date(),
                    synced = false
                )
                boatDao.updateBoat(updatedBoat)
                return ImportResult.Updated(existing.id)
            }
            return ImportResult.Skipped("Local version is newer or same age")
        }

        // Create new boat
        val newBoat = BoatEntity(
            id = shareData.data.id,
            name = shareData.data.name,
            enabled = shareData.data.enabled,
            isActive = false,
            synced = false,
            createdAt = Date(),
            lastModified = Date(),
            ownerId = null, // Shared boats don't have owner
            originSource = shareData.origin,
            originTimestamp = shareData.timestamp
        )
        boatDao.insertBoat(newBoat)
        return ImportResult.Created(newBoat.id)
    }
}
