package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.OfflineChangeEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OfflineChangeDao {
    @Query("SELECT * FROM offline_changes WHERE synced = 0 AND syncAttempts < 5 ORDER BY timestamp ASC")
    fun getPendingChanges(): Flow<List<OfflineChangeEntity>>

    @Query("SELECT * FROM offline_changes WHERE synced = 0 AND syncAttempts < 5 ORDER BY timestamp ASC")
    suspend fun getPendingChangesSync(): List<OfflineChangeEntity>

    @Query("SELECT * FROM offline_changes WHERE entityType = :entityType AND entityId = :entityId AND synced = 0")
    suspend fun getPendingChangesForEntity(entityType: String, entityId: String): List<OfflineChangeEntity>

    @Query("SELECT COUNT(*) FROM offline_changes WHERE synced = 0 AND syncAttempts < 5")
    fun getPendingChangeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM offline_changes WHERE synced = 0 AND syncAttempts >= 5")
    fun getFailedChangeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChange(change: OfflineChangeEntity)

    @Update
    suspend fun updateChange(change: OfflineChangeEntity)

    @Query("UPDATE offline_changes SET synced = 1, lastSyncAttempt = :syncTime WHERE id = :id")
    suspend fun markAsSynced(id: String, syncTime: Date)

    @Query("UPDATE offline_changes SET syncAttempts = syncAttempts + 1, lastSyncAttempt = :syncTime, syncError = :error WHERE id = :id")
    suspend fun incrementSyncAttempts(id: String, syncTime: Date, error: String)

    @Query("DELETE FROM offline_changes WHERE synced = 1 AND timestamp < :cutoffDate")
    suspend fun cleanupSyncedChanges(cutoffDate: Date): Int

    @Query("DELETE FROM offline_changes WHERE id = :id")
    suspend fun deleteChange(id: String)

    @Query("DELETE FROM offline_changes")
    suspend fun deleteAllChanges()
}