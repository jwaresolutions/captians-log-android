package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.GpsPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsPointDao {
    @Query("SELECT * FROM gps_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getGpsPointsForTrip(tripId: String): Flow<List<GpsPointEntity>>

    @Query("SELECT * FROM gps_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getGpsPointsForTripSync(tripId: String): List<GpsPointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpsPoint(gpsPoint: GpsPointEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpsPoints(gpsPoints: List<GpsPointEntity>)

    @Query("DELETE FROM gps_points WHERE tripId = :tripId")
    suspend fun deleteGpsPointsForTrip(tripId: String)

    @Query("SELECT * FROM gps_points")
    suspend fun getAllGpsPointsSync(): List<GpsPointEntity>
}
