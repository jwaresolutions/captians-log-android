package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.captainslog.database.converters.DateConverter
import java.util.Date
import java.util.UUID

@Entity(tableName = "marked_locations")
@TypeConverters(DateConverter::class)
data class MarkedLocationEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val category: String, // fishing, marina, anchorage, hazard, other
    val notes: String? = null,
    val tags: String = "", // Comma-separated tags
    val lastModified: Date = Date(),
    val createdAt: Date = Date(),
    val originSource: String? = null,    // Device UUID for P2P shares
    val originTimestamp: Long? = null   // Unix timestamp when shared/imported
)