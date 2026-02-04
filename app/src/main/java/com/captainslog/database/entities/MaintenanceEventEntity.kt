package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = "maintenance_events",
    foreignKeys = [
        ForeignKey(
            entity = MaintenanceTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["templateId"]),
        Index(value = ["dueDate"]),
        Index(value = ["completedAt"])
    ]
)
data class MaintenanceEventEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val templateId: String,
    val dueDate: Date,
    val completedAt: Date? = null,
    val actualCost: Double? = null,
    val actualTime: Int? = null, // in minutes
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)