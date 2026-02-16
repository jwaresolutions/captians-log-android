package com.captainslog.repository

import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.BoatEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository for managing boat data.
 * Handles local database operations only.
 */
class BoatRepository(
    private val database: AppDatabase
) {
    /**
     * Get all boats as a Flow for reactive updates
     */
    fun getAllBoats(): Flow<List<BoatEntity>> {
        return database.boatDao().getAllBoats()
    }

    /**
     * Get a specific boat by ID
     */
    suspend fun getBoatById(boatId: String): BoatEntity? {
        return database.boatDao().getBoatById(boatId)
    }

    /**
     * Get the currently active boat
     */
    suspend fun getActiveBoat(): BoatEntity? {
        return database.boatDao().getActiveBoat()
    }

    /**
     * Create a new boat locally
     * Checks for existing boats with same name to avoid duplicates
     */
    suspend fun createBoat(name: String): Result<BoatEntity> {
        return try {
            val trimmedName = name.trim()
            if (trimmedName.isEmpty()) {
                return Result.failure(Exception("Boat name cannot be empty"))
            }

            // Check if boat with same name already exists locally
            val existingBoats = database.boatDao().getAllBoatsSync()
            val existingBoat = existingBoats.find {
                it.name.equals(trimmedName, ignoreCase = true)
            }

            if (existingBoat != null) {
                return Result.failure(Exception("A boat with this name already exists"))
            }

            // Create boat locally
            val boat = BoatEntity(
                name = trimmedName,
                enabled = true,
                isActive = false,
                synced = false,
                lastModified = Date(),
                createdAt = Date()
            )

            database.boatDao().insertBoat(boat)
            Result.success(boat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update boat details (name, vessel details, owner info)
     */
    suspend fun updateBoatDetails(boat: BoatEntity): Result<Unit> {
        return try {
            val existingBoat = database.boatDao().getBoatById(boat.id)
            if (existingBoat != null) {
                val updatedBoat = boat.copy(
                    lastModified = Date()
                )
                database.boatDao().updateBoat(updatedBoat)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Boat not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update boat enabled status
     */
    suspend fun updateBoatStatus(boatId: String, enabled: Boolean): Result<Unit> {
        return try {
            val boat = database.boatDao().getBoatById(boatId)
            if (boat != null) {
                // If disabling an active boat, clear its active status
                val updatedBoat = if (!enabled && boat.isActive) {
                    boat.copy(enabled = enabled, isActive = false, lastModified = Date())
                } else {
                    boat.copy(enabled = enabled, lastModified = Date())
                }
                database.boatDao().updateBoat(updatedBoat)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Boat not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Set a boat as the active boat
     */
    suspend fun setActiveBoat(boatId: String): Result<Unit> {
        return try {
            // Clear all active boats first
            database.boatDao().clearActiveBoat()

            // Set the new active boat
            database.boatDao().setActiveBoat(boatId)

            // Mark as modified
            val boat = database.boatDao().getBoatById(boatId)
            if (boat != null) {
                database.boatDao().updateBoat(boat.copy(lastModified = Date()))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
