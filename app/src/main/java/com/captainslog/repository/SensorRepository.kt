package com.captainslog.repository

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.Date
/**
 * Repository for managing sensor types and sensor data
 */
class SensorRepository(
    private val connectionManager: ConnectionManager
) {
    companion object {
        private const val TAG = "SensorRepository"
    }
    
    /**
     * Create a new sensor type
     */
    suspend fun createSensorType(
        name: String,
        unit: String,
        loggingFrequency: String,
        description: String? = null
    ): Result<SensorTypeResponse> = withContext(Dispatchers.IO) {
        try {
            val request = CreateSensorTypeRequest(
                name = name,
                unit = unit,
                loggingFrequency = loggingFrequency,
                description = description
            )
            
            val apiService = connectionManager.getApiService()
            val response = apiService.createSensorType(request)
            
            if (response.isSuccessful && response.body() != null) {
                val sensorType = response.body()!!.data
                Log.d(TAG, "Created sensor type: ${sensorType.name}")
                Result.success(sensorType)
            } else {
                val error = "Failed to create sensor type: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sensor type", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get all sensor types
     */
    suspend fun getSensorTypes(): Result<List<SensorTypeResponse>> = withContext(Dispatchers.IO) {
        try {
            val apiService = connectionManager.getApiService()
            val response = apiService.getSensorTypes()
            
            if (response.isSuccessful && response.body() != null) {
                val sensorTypes = response.body()!!.data
                Log.d(TAG, "Retrieved ${sensorTypes.size} sensor types")
                Result.success(sensorTypes)
            } else {
                val error = "Failed to get sensor types: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sensor types", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get a specific sensor type by ID
     */
    suspend fun getSensorType(id: String): Result<SensorTypeResponse> = withContext(Dispatchers.IO) {
        try {
            val apiService = connectionManager.getApiService()
            val response = apiService.getSensorType(id)
            
            if (response.isSuccessful && response.body() != null) {
                val sensorType = response.body()!!.data
                Log.d(TAG, "Retrieved sensor type: ${sensorType.name}")
                Result.success(sensorType)
            } else {
                val error = "Failed to get sensor type: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sensor type", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update a sensor type
     */
    suspend fun updateSensorType(
        id: String,
        name: String? = null,
        unit: String? = null,
        loggingFrequency: String? = null,
        description: String? = null
    ): Result<SensorTypeResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateSensorTypeRequest(
                name = name,
                unit = unit,
                loggingFrequency = loggingFrequency,
                description = description
            )
            
            val apiService = connectionManager.getApiService()
            val response = apiService.updateSensorType(id, request)
            
            if (response.isSuccessful && response.body() != null) {
                val sensorType = response.body()!!.data
                Log.d(TAG, "Updated sensor type: ${sensorType.name}")
                Result.success(sensorType)
            } else {
                val error = "Failed to update sensor type: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating sensor type", e)
            Result.failure(e)
        }
    }
    
    /**
     * Record sensor data
     */
    suspend fun recordSensorData(
        tripId: String,
        sensorTypeName: String,
        value: Double,
        timestamp: Date = Date()
    ): Result<SensorReadingResponse> = withContext(Dispatchers.IO) {
        try {
            val request = CreateSensorReadingRequest(
                tripId = tripId,
                sensorTypeName = sensorTypeName,
                value = value,
                timestamp = timestamp
            )
            
            val apiService = connectionManager.getApiService()
            val response = apiService.createSensorReading(request)
            
            if (response.isSuccessful && response.body() != null) {
                val sensorReading = response.body()!!.data
                Log.d(TAG, "Recorded sensor data: $sensorTypeName = $value")
                Result.success(sensorReading)
            } else {
                val error = "Failed to record sensor data: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error recording sensor data", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get sensor readings for a trip
     */
    suspend fun getSensorReadings(
        tripId: String,
        sensorType: String? = null
    ): Result<List<SensorReadingResponse>> = withContext(Dispatchers.IO) {
        try {
            val apiService = connectionManager.getApiService()
            val response = apiService.getSensorReadings(tripId, sensorType)
            
            if (response.isSuccessful && response.body() != null) {
                val readings = response.body()!!.data
                Log.d(TAG, "Retrieved ${readings.size} sensor readings for trip $tripId")
                Result.success(readings)
            } else {
                val error = "Failed to get sensor readings: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sensor readings", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get sensor readings by sensor type ID
     */
    suspend fun getSensorReadingsByTypeId(
        tripId: String,
        sensorTypeId: String
    ): Result<List<SensorReadingResponse>> = withContext(Dispatchers.IO) {
        try {
            val apiService = connectionManager.getApiService()
            val response = apiService.getSensorReadingsByTypeId(tripId, sensorTypeId)
            
            if (response.isSuccessful && response.body() != null) {
                val readings = response.body()!!.data
                Log.d(TAG, "Retrieved ${readings.size} sensor readings for trip $tripId and sensor type $sensorTypeId")
                Result.success(readings)
            } else {
                val error = "Failed to get sensor readings by type ID: ${response.code()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sensor readings by type ID", e)
            Result.failure(e)
        }
    }
}