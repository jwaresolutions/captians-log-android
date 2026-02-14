package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import com.captainslog.database.converters.DateConverter
import java.util.Date

@Entity(tableName = "crew_members", primaryKeys = ["tripId", "deviceId"])
@TypeConverters(DateConverter::class)
data class CrewMemberEntity(
    val tripId: String,
    val deviceId: String,
    val displayName: String,
    val joinedAt: Date = Date(),
    val role: String = "crew"
)
