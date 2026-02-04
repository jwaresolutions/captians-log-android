package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.MaintenanceEventEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MaintenanceEventDao {
    @Query("SELECT * FROM maintenance_events WHERE completedAt IS NULL ORDER BY dueDate ASC")
    fun getUpcomingEvents(): Flow<List<MaintenanceEventEntity>>

    @Query("SELECT * FROM maintenance_events WHERE completedAt IS NOT NULL ORDER BY completedAt DESC")
    fun getCompletedEvents(): Flow<List<MaintenanceEventEntity>>

    @Query("SELECT * FROM maintenance_events WHERE templateId = :templateId ORDER BY dueDate ASC")
    fun getEventsByTemplate(templateId: String): Flow<List<MaintenanceEventEntity>>

    @Query("SELECT * FROM maintenance_events WHERE id = :id")
    fun getEventById(id: String): Flow<MaintenanceEventEntity?>

    @Query("SELECT * FROM maintenance_events WHERE id = :id")
    suspend fun getEventByIdSync(id: String): MaintenanceEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: MaintenanceEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<MaintenanceEventEntity>)

    @Update
    suspend fun updateEvent(event: MaintenanceEventEntity)

    @Delete
    suspend fun deleteEvent(event: MaintenanceEventEntity)

    @Query("DELETE FROM maintenance_events WHERE id = :id")
    suspend fun deleteEventById(id: String)

    @Query("UPDATE maintenance_events SET completedAt = :completedAt, actualCost = :actualCost, actualTime = :actualTime, notes = :notes WHERE id = :id")
    suspend fun completeEvent(id: String, completedAt: Date, actualCost: Double?, actualTime: Int?, notes: String?)

    @Query("DELETE FROM maintenance_events")
    suspend fun deleteAllEvents()

    @Query("SELECT * FROM maintenance_events")
    suspend fun getAllEventsSync(): List<MaintenanceEventEntity>
}