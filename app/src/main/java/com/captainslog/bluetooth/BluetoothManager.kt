package com.captainslog.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice as AndroidBluetoothDevice
import android.bluetooth.BluetoothManager as AndroidBluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * Manages Bluetooth connections and device discovery for Arduino sensor communication
 */
class BluetoothManager(private val context: Context) {
    
    companion object {
        private const val TAG = "BluetoothManager"
        
        // Standard Serial Port Profile UUID
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        
        // Buffer size for reading data
        private const val BUFFER_SIZE = 1024
    }
    
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as AndroidBluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    
    private val sensorDataParser = SensorDataParser()
    
    // State flows for UI observation
    private val _connectionState = MutableStateFlow(BluetoothConnectionState.DISCONNECTED)
    val connectionState: StateFlow<BluetoothConnectionState> = _connectionState.asStateFlow()
    
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()
    
    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices.asStateFlow()
    
    private val _sensorData = MutableStateFlow<List<SensorData>>(emptyList())
    val sensorData: StateFlow<List<SensorData>> = _sensorData.asStateFlow()
    
    // Connection management
    private var currentSocket: BluetoothSocket? = null
    private var currentDevice: AndroidBluetoothDevice? = null
    private var connectionJob: Job? = null
    private var dataReadingJob: Job? = null
    
    // Device discovery
    private var isDiscovering = false
    
    // Sensor data callback
    var onSensorDataReceived: ((SensorData) -> Unit)? = null
    
    // Broadcast receiver for device discovery
    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                AndroidBluetoothDevice.ACTION_FOUND -> {
                    val device: AndroidBluetoothDevice? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(AndroidBluetoothDevice.EXTRA_DEVICE, AndroidBluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(AndroidBluetoothDevice.EXTRA_DEVICE)
                    }
                    device?.let { addDiscoveredDevice(it) }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    isDiscovering = false
                    Log.d(TAG, "Device discovery finished")
                }
            }
        }
    }
    
    init {
        // Register discovery receiver
        val filter = IntentFilter().apply {
            addAction(AndroidBluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(discoveryReceiver, filter)
        
        // Load paired devices
        loadPairedDevices()
    }
    
    /**
     * Check if Bluetooth is supported and enabled
     */
    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }
    
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }
    
    /**
     * Check if required permissions are granted
     * On Android 12+ (API 31+): checks BLUETOOTH_CONNECT and BLUETOOTH_SCAN
     * On older versions: BLUETOOTH and BLUETOOTH_ADMIN are normal permissions (auto-granted)
     */
    fun hasRequiredPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            // BLUETOOTH and BLUETOOTH_ADMIN are normal permissions on older versions (auto-granted)
            true
        }
    }
    
    /**
     * Start device discovery
     */
    fun startDiscovery() {
        if (!hasRequiredPermissions()) {
            Log.w(TAG, "Missing Bluetooth permissions")
            return
        }
        
        if (!isBluetoothEnabled()) {
            Log.w(TAG, "Bluetooth is not enabled")
            return
        }
        
        if (isDiscovering) {
            Log.d(TAG, "Discovery already in progress")
            return
        }
        
        // Clear previous discoveries
        _discoveredDevices.value = emptyList()
        
        // Start discovery
        bluetoothAdapter?.let { adapter ->
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }
            
            val started = adapter.startDiscovery()
            if (started) {
                isDiscovering = true
                Log.d(TAG, "Started device discovery")
            } else {
                Log.w(TAG, "Failed to start device discovery")
            }
        }
    }
    
    /**
     * Stop device discovery
     */
    fun stopDiscovery() {
        if (!hasRequiredPermissions()) return
        
        bluetoothAdapter?.let { adapter ->
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
                isDiscovering = false
                Log.d(TAG, "Stopped device discovery")
            }
        }
    }
    
    /**
     * Load paired devices
     */
    private fun loadPairedDevices() {
        if (!hasRequiredPermissions()) return
        
        bluetoothAdapter?.let { adapter ->
            val pairedDevices = adapter.bondedDevices?.map { device ->
                BluetoothDevice.fromAndroidDevice(device)
            } ?: emptyList()
            
            _pairedDevices.value = pairedDevices
            Log.d(TAG, "Loaded ${pairedDevices.size} paired devices")
        }
    }
    
    /**
     * Add discovered device to list
     */
    private fun addDiscoveredDevice(device: AndroidBluetoothDevice) {
        if (!hasRequiredPermissions()) return
        
        val bluetoothDevice = BluetoothDevice.fromAndroidDevice(device)
        val currentDevices = _discoveredDevices.value.toMutableList()
        
        // Avoid duplicates
        if (currentDevices.none { it.address == bluetoothDevice.address }) {
            currentDevices.add(bluetoothDevice)
            _discoveredDevices.value = currentDevices
            Log.d(TAG, "Discovered device: ${bluetoothDevice.name} (${bluetoothDevice.address})")
        }
    }
    
    /**
     * Connect to a Bluetooth device
     */
    fun connectToDevice(device: BluetoothDevice) {
        if (!hasRequiredPermissions()) {
            Log.w(TAG, "Missing Bluetooth permissions")
            return
        }
        
        if (_connectionState.value == BluetoothConnectionState.CONNECTED) {
            Log.w(TAG, "Already connected to a device")
            return
        }
        
        // Stop discovery if running
        stopDiscovery()
        
        _connectionState.value = BluetoothConnectionState.CONNECTING
        currentDevice = device.device
        
        connectionJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Connecting to device: ${device.name}")
                
                val socket = device.device.createRfcommSocketToServiceRecord(SPP_UUID)
                currentSocket = socket
                
                socket.connect()
                
                _connectionState.value = BluetoothConnectionState.CONNECTED
                Log.i(TAG, "Connected to device: ${device.name}")
                
                // Start reading data
                startDataReading(socket)
                
            } catch (e: IOException) {
                Log.e(TAG, "Failed to connect to device: ${device.name}", e)
                _connectionState.value = BluetoothConnectionState.ERROR
                cleanup()
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception while connecting", e)
                _connectionState.value = BluetoothConnectionState.ERROR
                cleanup()
            }
        }
    }
    
    /**
     * Disconnect from current device
     */
    fun disconnect() {
        _connectionState.value = BluetoothConnectionState.DISCONNECTING
        
        connectionJob?.cancel()
        dataReadingJob?.cancel()
        
        cleanup()
        
        _connectionState.value = BluetoothConnectionState.DISCONNECTED
        Log.i(TAG, "Disconnected from device")
    }
    
    /**
     * Start reading data from connected device
     */
    private fun startDataReading(socket: BluetoothSocket) {
        dataReadingJob = CoroutineScope(Dispatchers.IO).launch {
            val inputStream: InputStream = socket.inputStream
            val buffer = ByteArray(BUFFER_SIZE)
            val stringBuilder = StringBuilder()
            
            try {
                while (isActive && socket.isConnected) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead > 0) {
                        val receivedData = String(buffer, 0, bytesRead)
                        stringBuilder.append(receivedData)
                        
                        // Process complete lines
                        processReceivedData(stringBuilder)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error reading data from device", e)
                if (isActive) {
                    _connectionState.value = BluetoothConnectionState.ERROR
                }
            }
        }
    }
    
    /**
     * Process received data and extract sensor readings
     */
    private fun processReceivedData(stringBuilder: StringBuilder) {
        val data = stringBuilder.toString()
        val lines = data.split('\n')
        
        // Process all complete lines (except the last one which might be incomplete)
        for (i in 0 until lines.size - 1) {
            val line = lines[i].trim()
            if (line.isNotEmpty()) {
                val sensorMessage = sensorDataParser.parseSensorData(line)
                if (sensorMessage != null) {
                    val sensorData = sensorMessage.toSensorData()
                    
                    // Add to sensor data list
                    val currentData = _sensorData.value.toMutableList()
                    currentData.add(sensorData)
                    
                    // Keep only last 1000 readings to prevent memory issues
                    if (currentData.size > 1000) {
                        currentData.removeAt(0)
                    }
                    
                    _sensorData.value = currentData
                    
                    // Notify callback
                    onSensorDataReceived?.invoke(sensorData)
                    
                    Log.d(TAG, "Received sensor data: ${sensorData.sensorType} = ${sensorData.value} ${sensorData.unit}")
                }
            }
        }
        
        // Keep the last incomplete line in the buffer
        stringBuilder.clear()
        if (lines.isNotEmpty()) {
            stringBuilder.append(lines.last())
        }
    }
    
    /**
     * Send data to connected device
     */
    fun sendData(data: String) {
        if (_connectionState.value != BluetoothConnectionState.CONNECTED) {
            Log.w(TAG, "Not connected to any device")
            return
        }
        
        currentSocket?.let { socket ->
            try {
                val outputStream: OutputStream = socket.outputStream
                outputStream.write(data.toByteArray())
                outputStream.flush()
                Log.d(TAG, "Sent data: $data")
            } catch (e: IOException) {
                Log.e(TAG, "Error sending data", e)
                _connectionState.value = BluetoothConnectionState.ERROR
            }
        }
    }
    
    /**
     * Get current sensor data buffer
     */
    fun getCurrentSensorData(): List<SensorData> {
        return _sensorData.value
    }
    
    /**
     * Clear sensor data buffer
     */
    fun clearSensorData() {
        _sensorData.value = emptyList()
    }
    
    /**
     * Cleanup resources
     */
    private fun cleanup() {
        try {
            currentSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing socket", e)
        }
        
        currentSocket = null
        currentDevice = null
    }
    
    /**
     * Release resources
     */
    fun release() {
        disconnect()
        
        try {
            context.unregisterReceiver(discoveryReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered
        }
        
        connectionJob?.cancel()
        dataReadingJob?.cancel()
    }
}