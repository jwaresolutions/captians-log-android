package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.captainslog.database.converters.DateConverter
import java.util.Date
import java.util.UUID

@Entity(tableName = "photos")
@TypeConverters(DateConverter::class)
data class PhotoEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val entityType: String, // trip, maintenance, note
    val entityId: String,
    val localPath: String,
    val mimeType: String,
    val sizeBytes: Long,
    val uploaded: Boolean = false,
    val uploadedAt: Date? = null,
    val createdAt: Date = Date(),
    val originSource: String? = null,    // Device UUID for P2P shares
    val originTimestamp: Long? = null   // Unix timestamp when shared/imported
)
