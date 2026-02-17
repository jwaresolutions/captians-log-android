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
    val role: String = "master", // master, mate, operator, deckhand, engineer, other
    val engineHours: Double? = null,
    val fuelConsumed: Double? = null,
    val weatherConditions: String? = null,
    val numberOfPassengers: Int? = null,
    val destination: String? = null,
    val lastModified: Date = Date(),
    val createdAt: Date = Date(),
    val captainId: String? = null,       // User ID of captain (null = local user is captain)
    val originSource: String? = null,    // Device UUID for P2P shares
    val originTimestamp: Long? = null,   // Unix timestamp when shared/imported
    val isReadOnly: Boolean = false,     // True for crew trips after disconnect
    val captainTripId: String? = null,   // On crew devices, links to captain's original trip ID for re-scan updates
    val captainName: String? = null,     // Display name of the captain
    // CG-719S form fields
    val bodyOfWater: String? = null,     // e.g., "Chesapeake Bay", "Gulf of Mexico"
    val boundaryClassification: String? = null, // great_lakes, shoreward, seaward
    val distanceOffshore: Double? = null // Nautical miles
)
