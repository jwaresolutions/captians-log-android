package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.captainslog.database.converters.DateConverter
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "gps_points",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
@TypeConverters(DateConverter::class)
data class GpsPointEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val heading: Float? = null,
    val timestamp: Date = Date()
)
