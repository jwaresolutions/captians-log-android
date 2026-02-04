package com.captainslog.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.bluetooth.BluetoothConnectionState
import com.captainslog.bluetooth.BluetoothDevice
import com.captainslog.bluetooth.BluetoothManager
import com.captainslog.bluetooth.SensorData
import com.captainslog.network.models.SensorTypeResponse
import com.captainslog.repository.SensorRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing sensor integration, Bluetooth connections, and sensor data
 */
class SensorViewModel(
    application: Application,
    private val sensorRepository: SensorRepository
) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "SensorViewModel"
    }
    
    private val bluetoothManager = BluetoothManager(application)
    
    // UI State
    private val _uiState = MutableStateFlow(SensorUiState())
    val uiState: StateFlow<SensorUiState> = _uiState.asStateFlow()
    
    // Bluetooth state flows
    val connectionState = bluetoothManager.connectionState
    val discoveredDevices = bluetoothManager.discoveredDevices
    val pairedDevices = bluetoothManager.pairedDevices
    val sensorData = bluetoothManager.sensorData
    
    // Sensor types from backend
    private val _sensorTypes = MutableStateFlow<List<SensorTypeResponse>>(emptyList())
    val sensorTypes: StateFlow<List<SensorTypeResponse>> = _sensorTypes.asStateFlow()
    
    // Current trip ID for sensor data recording
    private val _currentTripId = MutableStateFlow<String?>(null)
    val currentTripId: StateFlow<String?> = _currentTripId.asStateFlow()
    
    init {
        // Set up sensor data callback to relay to backend
        bluetoothManager.onSensorDataReceived = { sensorData ->
            relaySensorDataToBackend(sensorData)
        }
        
        // Load sensor types on initialization
        loadSensorTypes()
    }
    
    /**
     * Check if Bluetooth is supported and enabled
     */
    fun isBluetoothSupported(): Boolean = bluetoothManager.isBluetoothSupported()
    fun isBluetoothEnabled(): Boolean = bluetoothManager.isBluetoothEnabled()
    fun hasRequiredPermissions(): Boolean = bluetoothManager.hasRequiredPermissions()
    
    /**
     * Start device discovery
     */
    fun startDeviceDiscovery() {
        _uiState.value = _uiState.value.copy(isDiscovering = true)
        bluetoothManager.startDiscovery()
    }
    
    /**
     * Stop device discovery
     */
    fun stopDeviceDiscovery() {
        _uiState.value = _uiState.value.copy(isDiscovering = false)
        bluetoothManager.stopDiscovery()
    }
    
    /**
     * Connect to a Bluetooth device
     */
    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isConnecting = true,
                    connectionError = null
                )
                
                bluetoothManager.connectToDevice(device)
                
                // Wait for connection state to change
                connectionState.collect { state ->
                    when (state) {
                        BluetoothConnectionState.CONNECTED -> {
                            _uiState.value = _uiState.value.copy(
                                isConnecting = false,
                                connectedDevice = device,
                                connectionError = null
                            )
                            Log.i(TAG, "Successfully connected to ${device.name}")
                        }
                        BluetoothConnectionState.ERROR -> {
                            _uiState.value = _uiState.value.copy(
                                isConnecting = false,
                                connectionError = "Failed to connect to ${device.name}"
                            )
                            Log.e(TAG, "Failed to connect to ${device.name}")
                        }
                        else -> {
                            // Handle other states if needed
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isConnecting = false,
                    connectionError = "Connection error: ${e.message}"
                )
                Log.e(TAG, "Error connecting to device", e)
            }
        }
    }
    
    /**
     * Disconnect from current device
     */
    fun disconnect() {
        bluetoothManager.disconnect()
        _uiState.value = _uiState.value.copy(
            connectedDevice = null,
            connectionError = null
        )
    }
    
    /**
     * Load sensor types from backend
     */
    fun loadSensorTypes() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingSensorTypes = true)
                
                val result = sensorRepository.getSensorTypes()
                result.fold(
                    onSuccess = { types ->
                        _sensorTypes.value = types
                        _uiState.value = _uiState.value.copy(
                            isLoadingSensorTypes = false,
                            sensorTypesError = null
                        )
                        Log.d(TAG, "Loaded ${types.size} sensor types")
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingSensorTypes = false,
                            sensorTypesError = "Failed to load sensor types: ${error.message}"
                        )
                        Log.e(TAG, "Failed to load sensor types", error)
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingSensorTypes = false,
                    sensorTypesError = "Error loading sensor types: ${e.message}"
                )
                Log.e(TAG, "Error loading sensor types", e)
            }
        }
    }
    
    /**
     * Create a new sensor type
     */
    fun createSensorType(
        name: String,
        unit: String,
        loggingFrequency: String,
        description: String? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isCreatingSensorType = true)
                
                val result = sensorRepository.createSensorType(name, unit, loggingFrequency, description)
                result.fold(
                    onSuccess = { sensorType ->
                        // Reload sensor types to include the new one
                        loadSensorTypes()
                        _uiState.value = _uiState.value.copy(
                            isCreatingSensorType = false,
                            sensorTypeCreationError = null
                        )
                        Log.i(TAG, "Created sensor type: ${sensorType.name}")
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isCreatingSensorType = false,
                            sensorTypeCreationError = "Failed to create sensor type: ${error.message}"
                        )
                        Log.e(TAG, "Failed to create sensor type", error)
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreatingSensorType = false,
                    sensorTypeCreationError = "Error creating sensor type: ${e.message}"
                )
                Log.e(TAG, "Error creating sensor type", e)
            }
        }
    }
    
    /**
     * Set the current trip ID for sensor data recording
     */
    fun setCurrentTripId(tripId: String?) {
        _currentTripId.value = tripId
        Log.d(TAG, "Set current trip ID: $tripId")
    }
    
    /**
     * Relay sensor data to backend API
     */
    private fun relaySensorDataToBackend(sensorData: SensorData) {
        val tripId = _currentTripId.value
        if (tripId == null) {
            Log.w(TAG, "No current trip ID set, skipping sensor data relay")
            return
        }
        
        viewModelScope.launch {
            try {
                val result = sensorRepository.recordSensorData(
                    tripId = tripId,
                    sensorTypeName = sensorData.sensorType,
                    value = sensorData.value,
                    timestamp = java.util.Date(sensorData.timestamp)
                )
                
                result.fold(
                    onSuccess = { reading ->
                        Log.d(TAG, "Successfully relayed sensor data: ${reading.id}")
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Failed to relay sensor data: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error relaying sensor data", e)
            }
        }
    }
    
    /**
     * Send data to connected device
     */
    fun sendDataToDevice(data: String) {
        bluetoothManager.sendData(data)
    }
    
    /**
     * Clear sensor data buffer
     */
    fun clearSensorData() {
        bluetoothManager.clearSensorData()
    }
    
    /**
     * Clear connection error
     */
    fun clearConnectionError() {
        _uiState.value = _uiState.value.copy(connectionError = null)
    }
    
    /**
     * Clear sensor types error
     */
    fun clearSensorTypesError() {
        _uiState.value = _uiState.value.copy(sensorTypesError = null)
    }
    
    /**
     * Clear sensor type creation error
     */
    fun clearSensorTypeCreationError() {
        _uiState.value = _uiState.value.copy(sensorTypeCreationError = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        bluetoothManager.release()
    }
}

/**
 * UI state for sensor management screen
 */
data class SensorUiState(
    val isDiscovering: Boolean = false,
    val isConnecting: Boolean = false,
    val connectedDevice: BluetoothDevice? = null,
    val connectionError: String? = null,
    val isLoadingSensorTypes: Boolean = false,
    val sensorTypesError: String? = null,
    val isCreatingSensorType: Boolean = false,
    val sensorTypeCreationError: String? = null
)