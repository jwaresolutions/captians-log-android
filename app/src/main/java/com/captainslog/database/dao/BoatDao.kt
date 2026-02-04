package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.BoatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoatDao {
    @Query("SELECT * FROM boats ORDER BY createdAt DESC")
    fun getAllBoats(): Flow<List<BoatEntity>>

    @Query("SELECT * FROM boats ORDER BY createdAt DESC")
    suspend fun getAllBoatsSync(): List<BoatEntity>

    @Query("SELECT * FROM boats WHERE id = :boatId")
    suspend fun getBoatById(boatId: String): BoatEntity?

    @Query("SELECT * FROM boats WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveBoat(): BoatEntity?

    @Query("SELECT * FROM boats WHERE synced = 0")
    suspend fun getUnsyncedBoats(): List<BoatEntity>

    @Query("SELECT * FROM boats WHERE ownerId = :userId OR ownerId IS NULL")
    fun getOwnedBoats(userId: String): Flow<List<BoatEntity>>

    @Query("SELECT * FROM boats WHERE originSource = :originSource AND id = :originalId LIMIT 1")
    suspend fun getBoatByOrigin(originSource: String, originalId: String): BoatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoat(boat: BoatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoats(boats: List<BoatEntity>)

    @Update
    suspend fun updateBoat(boat: BoatEntity)

    @Delete
    suspend fun deleteBoat(boat: BoatEntity)

    @Query("UPDATE boats SET synced = 1 WHERE id = :boatId")
    suspend fun markAsSynced(boatId: String)

    @Query("UPDATE boats SET isActive = 0")
    suspend fun clearActiveBoat()

    @Query("UPDATE boats SET isActive = 1 WHERE id = :boatId")
    suspend fun setActiveBoat(boatId: String)
}
