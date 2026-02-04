package com.captainslog.sharing.models

import com.google.gson.annotations.SerializedName

data class TripExportData(
    @SerializedName("version") val version: Int = 1,
    @SerializedName("type") val type: String = "trip",
    @SerializedName("origin") val origin: String,
    @SerializedName("exportedAt") val exportedAt: Long,
    @SerializedName("trip") val trip: TripData,
    @SerializedName("boat") val boat: BoatData? = null,  // Include boat info
    @SerializedName("gpsPoints") val gpsPoints: List<GpsPointData>,
    @SerializedName("notes") val notes: List<NoteData>,
    @SerializedName("photos") val photos: List<PhotoData>
)

data class TripData(
    val id: String,
    val boatId: String,
    val startTime: Long,
    val endTime: Long?,
    val waterType: String?,
    val role: String?,
    val weatherConditions: String?,
    val destination: String?,
    val passengerCount: Int?,
    val fuelConsumed: Double?
)

data class GpsPointData(
    val lat: Double,
    val lng: Double,
    val ts: Long,
    val speed: Float?,
    val heading: Float?
)

data class NoteData(
    val id: String,
    val content: String,
    val createdAt: Long,
    val noteType: String?
)

data class PhotoData(
    val id: String,
    val filename: String,
    val caption: String?,
    val base64: String?  // Optional: for small photos, include base64 encoded data
)
