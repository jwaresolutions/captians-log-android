package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = "maintenance_templates",
    foreignKeys = [
        ForeignKey(
            entity = BoatEntity::class,
            parentColumns = ["id"],
            childColumns = ["boatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["boatId"]),
        Index(value = ["isActive"]),
        Index(value = ["createdAt"])
    ]
)
data class MaintenanceTemplateEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val boatId: String,
    val title: String,
    val description: String,
    val component: String,
    val estimatedCost: Double? = null,
    val estimatedTime: Int? = null, // in minutes
    val isActive: Boolean = true,
    val recurrenceType: String, // 'days', 'weeks', 'months', 'years', 'engine_hours'
    val recurrenceInterval: Int,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)