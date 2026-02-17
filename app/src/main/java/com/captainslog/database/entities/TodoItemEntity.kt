package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "todo_items",
    foreignKeys = [
        ForeignKey(
            entity = TodoListEntity::class,
            parentColumns = ["id"],
            childColumns = ["todoListId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["todoListId"]),
        Index(value = ["completed"]),
        Index(value = ["createdAt"])
    ]
)
data class TodoItemEntity(
    @PrimaryKey
    val id: String,
    val todoListId: String,
    val content: String,
    val completed: Boolean = false,
    val completedAt: Date? = null,
    val createdAt: Date,
    val updatedAt: Date
)