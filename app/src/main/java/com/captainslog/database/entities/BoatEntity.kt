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
    val lastModified: Date = Date(),
    val createdAt: Date = Date(),
    val ownerId: String? = null,
    val originSource: String? = null,
    val originTimestamp: Long? = null,
    // Vessel details for CG-719S form
    val officialNumber: String? = null,
    val grossTons: Double? = null,
    val lengthFeet: Int? = null,
    val lengthInches: Int? = null,
    val widthFeet: Int? = null,
    val widthInches: Int? = null,
    val depthFeet: Int? = null,
    val depthInches: Int? = null,
    val propulsionType: String? = null, // motor, steam, gas_turbine, sail, aux_sail
    // Owner/Operator info for CG-719S form
    val ownerFirstName: String? = null,
    val ownerMiddleName: String? = null,
    val ownerLastName: String? = null,
    val ownerStreetAddress: String? = null,
    val ownerCity: String? = null,
    val ownerState: String? = null,
    val ownerZipCode: String? = null,
    val ownerEmail: String? = null,
    val ownerPhone: String? = null
)
