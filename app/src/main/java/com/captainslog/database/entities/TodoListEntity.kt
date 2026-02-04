package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "todo_lists",
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
        Index(value = ["createdAt"])
    ]
)
data class TodoListEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val boatId: String? = null, // Optional - only for boat-specific lists
    val createdAt: Date,
    val updatedAt: Date,
    val synced: Boolean = false,
    val originSource: String? = null,    // Device UUID for P2P shares
    val originTimestamp: Long? = null   // Unix timestamp when shared/imported
)