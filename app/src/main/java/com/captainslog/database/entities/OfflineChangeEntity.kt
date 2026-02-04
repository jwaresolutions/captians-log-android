package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = "offline_changes",
    indices = [
        Index(value = ["entityType", "entityId"]),
        Index(value = ["synced"]),
        Index(value = ["timestamp"])
    ]
)
data class OfflineChangeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val entityType: String, // 'maintenance_template'
    val entityId: String,
    val changeType: String, // 'schedule_change', 'template_update', 'template_create', 'template_delete'
    val changeData: String, // JSON string of the change data
    val timestamp: Date = Date(),
    val synced: Boolean = false,
    val syncAttempts: Int = 0,
    val lastSyncAttempt: Date? = null,
    val syncError: String? = null
)