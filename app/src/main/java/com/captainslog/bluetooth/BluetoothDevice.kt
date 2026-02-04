package com.captainslog.bluetooth

import android.bluetooth.BluetoothDevice as AndroidBluetoothDevice

/**
 * Wrapper for Android BluetoothDevice with additional metadata
 */
data class BluetoothDevice(
    val device: AndroidBluetoothDevice,
    val name: String,
    val address: String,
    val bondState: Int,
    val isConnected: Boolean = false,
    val signalStrength: Int? = null
) {
    companion object {
        @Suppress("MissingPermission")
        fun fromAndroidDevice(device: AndroidBluetoothDevice): BluetoothDevice {
            return BluetoothDevice(
                device = device,
                name = device.name ?: "Unknown Device",
                address = device.address,
                bondState = device.bondState,
                isConnected = false
            )
        }
    }
}

/**
 * Bluetooth connection state
 */
enum class BluetoothConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

/**
 * Sensor data received from Arduino
 */
data class SensorData(
    val sensorType: String,
    val value: Double,
    val unit: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Parsed sensor message from Arduino
 */
data class SensorMessage(
    val sensorType: String,
    val value: Double,
    val unit: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toSensorData(): SensorData {
        return SensorData(
            sensorType = sensorType,
            value = value,
            unit = unit ?: "unknown",
            timestamp = timestamp
        )
    }
}