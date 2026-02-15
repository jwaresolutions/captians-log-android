package com.captainslog.sharing

import com.captainslog.database.dao.BoatDao
import com.captainslog.database.dao.CrewMemberDao
import com.captainslog.database.dao.TripDao
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.CrewMemberEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.models.TripCrewShareData
import java.util.Date
import java.util.UUID

/**
 * Handles importing trips from crew join QR codes.
 */
class TripCrewImporter(
    private val tripDao: TripDao,
    private val boatDao: BoatDao,
    private val crewMemberDao: CrewMemberDao,
    private val securePreferences: SecurePreferences
) {
    /**
     * Imports a trip from crew share data, handling duplicates and updates.
     *
     * @param shareData The trip crew data from the scanned QR code
     * @return ImportResult indicating what happened
     */
    suspend fun importTrip(shareData: TripCrewShareData): ImportResult {
        // Prevent captain from joining their own trip
        if (shareData.origin == "device:${securePreferences.deviceId}") {
            return ImportResult.Skipped("Cannot join your own trip")
        }

        // Check if trip already exists via captain's trip ID
        val existing = tripDao.getTripByCaptainTripId(shareData.data.tripId)

        if (existing != null) {
            // Update if incoming is newer
            if (shareData.timestamp > (existing.originTimestamp ?: 0)) {
                val updatedTrip = existing.copy(
                    endTime = shareData.data.endTime?.let { Date(it) },
                    originTimestamp = shareData.timestamp,
                    lastModified = Date(),
                    synced = false
                )
                tripDao.updateTrip(updatedTrip)

                // Update crew list
                crewMemberDao.deleteCrewForTrip(existing.id)
                insertCrewMembers(existing.id, shareData)

                return ImportResult.Updated(existing.id)
            }
            return ImportResult.Skipped("Already joined this trip")
        }

        // Create or update boat
        val boat = boatDao.getBoatById(shareData.data.boatId)
        if (boat == null) {
            val newBoat = BoatEntity(
                id = shareData.data.boatId,
                name = shareData.data.boatName,
                enabled = true,
                isActive = false,
                synced = false,
                createdAt = Date(),
                lastModified = Date(),
                ownerId = null,
                originSource = shareData.origin,
                originTimestamp = shareData.timestamp
            )
            boatDao.insertBoat(newBoat)
        }

        // Create new trip
        val newTripId = UUID.randomUUID().toString()
        val newTrip = TripEntity(
            id = newTripId,
            boatId = shareData.data.boatId,
            startTime = Date(shareData.data.startTime),
            endTime = shareData.data.endTime?.let { Date(it) },
            waterType = shareData.data.waterType,
            role = "deckhand",
            captainId = shareData.origin,
            captainTripId = shareData.data.tripId,
            captainName = shareData.data.captainName,
            synced = false,
            originSource = shareData.origin,
            originTimestamp = shareData.timestamp,
            isReadOnly = false,
            createdAt = Date(),
            lastModified = Date()
        )
        tripDao.insertTrip(newTrip)

        // Insert crew members
        insertCrewMembers(newTripId, shareData)

        return ImportResult.Created(newTripId)
    }

    private suspend fun insertCrewMembers(tripId: String, shareData: TripCrewShareData) {
        // Insert crew from share data
        val crewMembers = shareData.data.crew.map { crewData ->
            CrewMemberEntity(
                tripId = tripId,
                deviceId = crewData.deviceId,
                displayName = crewData.name,
                joinedAt = Date(crewData.joinedAt),
                role = "crew"
            )
        }

        // Add self as crew member
        val selfCrewMember = CrewMemberEntity(
            tripId = tripId,
            deviceId = securePreferences.deviceId,
            displayName = securePreferences.displayName ?: "Me",
            joinedAt = Date(),
            role = "crew"
        )

        crewMemberDao.insertCrewMembers(crewMembers + selfCrewMember)
    }
}
