package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.TripEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripEntity?

    @Query("SELECT * FROM trips WHERE synced = 0")
    suspend fun getUnsyncedTrips(): List<TripEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("UPDATE trips SET synced = 1 WHERE id = :tripId")
    suspend fun markAsSynced(tripId: String)

    @Query("SELECT * FROM trips WHERE endTime IS NULL ORDER BY startTime DESC")
    suspend fun getActiveTrips(): List<TripEntity>

    @Query("SELECT * FROM trips WHERE captainId = :userId OR captainId IS NULL ORDER BY startTime DESC")
    fun getTripsForUser(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isReadOnly = 1 ORDER BY startTime DESC")
    fun getReadOnlyTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isReadOnly = 0 ORDER BY startTime DESC")
    fun getEditableTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE originSource = :originSource AND id = :originalId LIMIT 1")
    suspend fun getTripByOrigin(originSource: String, originalId: String): TripEntity?

    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    suspend fun getAllTripsSync(): List<TripEntity>

    @Query("SELECT * FROM trips WHERE captainTripId = :captainTripId LIMIT 1")
    suspend fun getTripByCaptainTripId(captainTripId: String): TripEntity?

    @Query("SELECT * FROM trips WHERE boatId = :boatId AND startTime <= :endTime AND endTime >= :startTime")
    suspend fun getOverlappingTrips(boatId: String, startTime: Date, endTime: Date): List<TripEntity>
}
