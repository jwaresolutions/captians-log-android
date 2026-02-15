package com.captainslog.sharing.models

import com.google.gson.annotations.SerializedName

data class TripCrewShareData(
    @SerializedName("v") val version: Int = 1,
    @SerializedName("type") val type: String = "crew_join",
    @SerializedName("origin") val origin: String,  // "device:$deviceId"
    @SerializedName("ts") val timestamp: Long,
    @SerializedName("data") val data: TripCrewData
)

data class TripCrewData(
    val tripId: String,
    val boatId: String,
    val boatName: String,
    val captainName: String,
    val startTime: Long,       // Unix millis
    val endTime: Long? = null,  // null if trip still active
    val waterType: String,
    val crew: List<CrewMemberData> = emptyList()
)

data class CrewMemberData(
    val deviceId: String,
    val name: String,
    val joinedAt: Long  // Unix millis
)

data class CrewResponseData(
    @SerializedName("v") val version: Int = 1,
    @SerializedName("type") val type: String = "crew_response",
    @SerializedName("origin") val origin: String = "",
    @SerializedName("ts") val timestamp: Long = 0,
    @SerializedName("tripId") val tripId: String,
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("joinedAt") val joinedAt: Long
)
