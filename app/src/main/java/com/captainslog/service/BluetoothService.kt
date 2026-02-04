package com.captainslog.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.captainslog.bluetooth.BluetoothManager
import com.captainslog.bluetooth.SensorData
import com.captainslog.network.ApiService
import com.captainslog.network.models.CreateSensorReadingRequest
import com.captainslog.repository.TripRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * Background service for managing Bluetooth connections and sensor data collection
 */
class BluetoothService : Service() {
    
    companion object {
        private const val TAG = "BluetoothService"
        
        // Buffer sensor data for batch upload
        private const val BATCH_SIZE = 10
        private const val BATCH_TIMEOUT_MS = 30000L // 30 seconds
    }
    
    // Service binder
    private val binder = BluetoothServiceBinder()
    
    // Bluetooth manager
    private lateinit var bluetoothManager: BluetoothManager
    
    // Dependencies - will be set by the service client
    private var apiService: ApiService? = null
    private var tripRepository: TripRepository? = null
    
    // Coroutine scope for service operations
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Sensor data buffer for batch processing
    private val sensorDataBuffer = ConcurrentLinkedQueue<SensorData>()
    private var batchUploadJob: Job? = null
    
    // Current active trip ID
    private var currentTripId: String? = null
    
    inner class BluetoothServiceBinder : Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BluetoothService created")
        
        // Initialize Bluetooth manager
        bluetoothManager = BluetoothManager(this)
        
        // Set up sensor data callback
        bluetoothManager.onSensorDataReceived = { sensorData ->
            handleSensorData(sensorData)
        }
        
        // Start batch upload job
        startBatchUploadJob()
    }
    
    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "BluetoothService bound")
        return binder
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "BluetoothService destroyed")
        
        // Clean up resources
        bluetoothManager.release()
        batchUploadJob?.cancel()
        serviceScope.cancel()
    }
    
    /**
     * Get the Bluetooth manager instance
     */
    fun getBluetoothManager(): BluetoothManager {
        return bluetoothManager
    }
    
    /**
     * Set dependencies (called by service client)
     */
    fun setDependencies(apiService: ApiService, tripRepository: TripRepository) {
        this.apiService = apiService
        this.tripRepository = tripRepository
    }
    
    /**
     * Set the current active trip ID for sensor data association
     */
    fun setCurrentTripId(tripId: String?) {
        currentTripId = tripId
        Log.d(TAG, "Current trip ID set to: $tripId")
    }
    
    /**
     * Get the current active trip ID
     */
    fun getCurrentTripId(): String? {
        return currentTripId
    }
    
    /**
     * Handle incoming sensor data
     */
    private fun handleSensorData(sensorData: SensorData) {
        Log.d(TAG, "Handling sensor data: ${sensorData.sensorType} = ${sensorData.value}")
        
        // Only buffer data if we have an active trip
        if (currentTripId != null) {
            sensorDataBuffer.offer(sensorData)
            Log.d(TAG, "Buffered sensor data. Buffer size: ${sensorDataBuffer.size}")
        } else {
            Log.w(TAG, "No active trip - discarding sensor data")
        }
    }
    
    /**
     * Start batch upload job for sensor data
     */
    private fun startBatchUploadJob() {
        batchUploadJob = serviceScope.launch {
            while (isActive) {
                try {
                    // Wait for batch timeout or until we have enough data
                    delay(BATCH_TIMEOUT_MS)
                    
                    if (sensorDataBuffer.isNotEmpty()) {
                        uploadSensorDataBatch()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in batch upload job", e)
                }
            }
        }
    }
    
    /**
     * Upload a batch of sensor data to the backend
     */
    private suspend fun uploadSensorDataBatch() {
        val tripId = currentTripId
        if (tripId == null) {
            Log.w(TAG, "No active trip - clearing sensor data buffer")
            sensorDataBuffer.clear()
            return
        }
        
        val dataToUpload = mutableListOf<SensorData>()
        
        // Extract data from buffer (up to batch size)
        repeat(BATCH_SIZE) {
            val data = sensorDataBuffer.poll()
            if (data != null) {
                dataToUpload.add(data)
            }
        }
        
        if (dataToUpload.isEmpty()) {
            return
        }
        
        Log.d(TAG, "Uploading ${dataToUpload.size} sensor readings for trip $tripId")
        
        val api = apiService
        if (api == null) {
            Log.w(TAG, "ApiService not set - cannot upload sensor data")
            // Re-add all data to buffer for retry when API service is available
            dataToUpload.forEach { sensorDataBuffer.offer(it) }
            return
        }
        
        try {
            // Upload each sensor reading
            for (sensorData in dataToUpload) {
                val request = CreateSensorReadingRequest(
                    tripId = tripId,
                    sensorTypeName = sensorData.sensorType,
                    value = sensorData.value,
                    timestamp = Date(sensorData.timestamp)
                )
                
                val response = api.createSensorReading(request)
                if (response.isSuccessful) {
                    Log.d(TAG, "Successfully uploaded sensor reading: ${sensorData.sensorType}")
                } else {
                    Log.w(TAG, "Failed to upload sensor reading: ${response.code()}")
                    // Re-add to buffer for retry
                    sensorDataBuffer.offer(sensorData)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading sensor data batch", e)
            // Re-add all data to buffer for retry
            dataToUpload.forEach { sensorDataBuffer.offer(it) }
        }
    }
    
    /**
     * Force upload of all buffered sensor data
     */
    fun flushSensorData() {
        serviceScope.launch {
            while (sensorDataBuffer.isNotEmpty()) {
                uploadSensorDataBatch()
            }
        }
    }
    
    /**
     * Get the current sensor data buffer size
     */
    fun getBufferSize(): Int {
        return sensorDataBuffer.size
    }
    
    /**
     * Clear the sensor data buffer
     */
    fun clearBuffer() {
        sensorDataBuffer.clear()
        Log.d(TAG, "Sensor data buffer cleared")
    }
    
    /**
     * Get connection state flow for UI observation
     */
    fun getConnectionState(): StateFlow<com.captainslog.bluetooth.BluetoothConnectionState> {
        return bluetoothManager.connectionState
    }
    
    /**
     * Get discovered devices flow for UI observation
     */
    fun getDiscoveredDevices(): StateFlow<List<com.captainslog.bluetooth.BluetoothDevice>> {
        return bluetoothManager.discoveredDevices
    }
    
    /**
     * Get paired devices flow for UI observation
     */
    fun getPairedDevices(): StateFlow<List<com.captainslog.bluetooth.BluetoothDevice>> {
        return bluetoothManager.pairedDevices
    }
    
    /**
     * Get sensor data flow for UI observation
     */
    fun getSensorData(): StateFlow<List<SensorData>> {
        return bluetoothManager.sensorData
    }
}