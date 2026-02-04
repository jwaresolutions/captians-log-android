package com.captainslog.network.models

import com.google.gson.annotations.SerializedName
import java.util.Date

// Sensor Type Models
data class SensorTypeResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("loggingFrequency") val loggingFrequency: String,
    @SerializedName("description") val description: String?,
    @SerializedName("createdAt") val createdAt: Date,
    @SerializedName("updatedAt") val updatedAt: Date
)

data class CreateSensorTypeRequest(
    @SerializedName("name") val name: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("loggingFrequency") val loggingFrequency: String,
    @SerializedName("description") val description: String? = null
)

data class UpdateSensorTypeRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("unit") val unit: String? = null,
    @SerializedName("loggingFrequency") val loggingFrequency: String? = null,
    @SerializedName("description") val description: String? = null
)

// Sensor Reading Models
data class SensorReadingResponse(
    @SerializedName("id") val id: String,
    @SerializedName("tripId") val tripId: String,
    @SerializedName("sensorTypeId") val sensorTypeId: String,
    @SerializedName("value") val value: Double,
    @SerializedName("unit") val unit: String,
    @SerializedName("timestamp") val timestamp: Date,
    @SerializedName("sensorType") val sensorType: SensorTypeResponse?,
    @SerializedName("createdAt") val createdAt: Date
)

data class CreateSensorReadingRequest(
    @SerializedName("tripId") val tripId: String,
    @SerializedName("sensorTypeName") val sensorTypeName: String,
    @SerializedName("value") val value: Double,
    @SerializedName("timestamp") val timestamp: Date
)

// Response wrappers
data class SensorTypeListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<SensorTypeResponse>,
    @SerializedName("message") val message: String? = null
)

data class SensorReadingListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<SensorReadingResponse>,
    @SerializedName("message") val message: String? = null
)

data class SingleSensorTypeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SensorTypeResponse,
    @SerializedName("message") val message: String? = null
)

data class SingleSensorReadingResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SensorReadingResponse,
    @SerializedName("message") val message: String? = null
)