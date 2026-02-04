package com.captainslog.sharing.models

import com.google.gson.annotations.SerializedName

data class BoatShareData(
    @SerializedName("v") val version: Int = 1,
    @SerializedName("type") val type: String = "boat",
    @SerializedName("origin") val origin: String,  // "device:$deviceId"
    @SerializedName("ts") val timestamp: Long,     // Unix timestamp
    @SerializedName("data") val data: BoatData
)

data class BoatData(
    val id: String,
    val name: String,
    val enabled: Boolean,
    val boatType: String? = null,
    val registrationNumber: String? = null,
    val fuelCapacity: Double? = null,
    val engineHours: Double? = null
)
