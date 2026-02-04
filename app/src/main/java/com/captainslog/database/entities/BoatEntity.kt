package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.captainslog.database.converters.DateConverter
import java.util.Date
import java.util.UUID

@Entity(tableName = "boats")
@TypeConverters(DateConverter::class)
data class BoatEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val enabled: Boolean = true,
    val isActive: Boolean = false,
    val synced: Boolean = false,
    val lastModified: Date = Date(),
    val createdAt: Date = Date(),
    val ownerId: String? = null,
    val originSource: String? = null,
    val originTimestamp: Long? = null
)
