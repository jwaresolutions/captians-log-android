package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.captainslog.database.converters.DateConverter
import java.util.Date
import java.util.UUID

@Entity(tableName = "trips")
@TypeConverters(DateConverter::class)
data class TripEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val boatId: String,
    val startTime: Date,
    val endTime: Date? = null,
    val waterType: String = "inland", // inland, coastal, offshore
    val role: String = "captain", // captain, crew, observer
    val engineHours: Double? = null,
    val fuelConsumed: Double? = null,
    val weatherConditions: String? = null,
    val numberOfPassengers: Int? = null,
    val destination: String? = null,
    val synced: Boolean = false,
    val lastModified: Date = Date(),
    val createdAt: Date = Date(),
    val captainId: String? = null,       // User ID of captain (null = local user is captain)
    val originSource: String? = null,    // Device UUID for P2P shares
    val originTimestamp: Long? = null,   // Unix timestamp when shared/imported
    val isReadOnly: Boolean = false      // True for crew trips after disconnect
)
