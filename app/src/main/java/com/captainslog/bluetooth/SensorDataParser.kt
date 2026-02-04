package com.captainslog.bluetooth

import android.util.Log
import java.util.regex.Pattern

/**
 * Parser for sensor data received from Arduino via Bluetooth
 * 
 * Expected format examples:
 * - "FUEL:75.5:L"
 * - "BATTERY:12.6:V"
 * - "TEMP:23.4:C"
 * - "BILGE:0:BOOL"
 * 
 * Format: SENSOR_TYPE:VALUE:UNIT
 */
class SensorDataParser {
    
    companion object {
        private const val TAG = "SensorDataParser"
        
        // Pattern to match sensor data: SENSOR_TYPE:VALUE:UNIT
        private val SENSOR_DATA_PATTERN = Pattern.compile("^([A-Z_]+):([+-]?\\d*\\.?\\d+):([A-Z_]+)$")
        
        // Alternative pattern for simple format: SENSOR_TYPE:VALUE
        private val SIMPLE_SENSOR_PATTERN = Pattern.compile("^([A-Z_]+):([+-]?\\d*\\.?\\d+)$")
    }
    
    /**
     * Parse a single line of sensor data
     */
    fun parseSensorData(rawData: String): SensorMessage? {
        val trimmedData = rawData.trim()
        
        if (trimmedData.isEmpty()) {
            return null
        }
        
        Log.d(TAG, "Parsing sensor data: $trimmedData")
        
        // Try full format first: SENSOR_TYPE:VALUE:UNIT
        val fullMatcher = SENSOR_DATA_PATTERN.matcher(trimmedData)
        if (fullMatcher.matches()) {
            val sensorType = fullMatcher.group(1)!!
            val valueStr = fullMatcher.group(2)!!
            val unit = fullMatcher.group(3)!!
            
            return try {
                val value = valueStr.toDouble()
                SensorMessage(
                    sensorType = sensorType.lowercase(),
                    value = value,
                    unit = unit.lowercase(),
                    timestamp = System.currentTimeMillis()
                )
            } catch (e: NumberFormatException) {
                Log.w(TAG, "Failed to parse sensor value: $valueStr", e)
                null
            }
        }
        
        // Try simple format: SENSOR_TYPE:VALUE
        val simpleMatcher = SIMPLE_SENSOR_PATTERN.matcher(trimmedData)
        if (simpleMatcher.matches()) {
            val sensorType = simpleMatcher.group(1)!!
            val valueStr = simpleMatcher.group(2)!!
            
            return try {
                val value = valueStr.toDouble()
                SensorMessage(
                    sensorType = sensorType.lowercase(),
                    value = value,
                    unit = null,
                    timestamp = System.currentTimeMillis()
                )
            } catch (e: NumberFormatException) {
                Log.w(TAG, "Failed to parse sensor value: $valueStr", e)
                null
            }
        }
        
        Log.w(TAG, "Failed to parse sensor data: $trimmedData")
        return null
    }
    
    /**
     * Parse multiple lines of sensor data
     */
    fun parseMultipleSensorData(rawData: String): List<SensorMessage> {
        val lines = rawData.split('\n', '\r')
        val results = mutableListOf<SensorMessage>()
        
        for (line in lines) {
            val sensorMessage = parseSensorData(line)
            if (sensorMessage != null) {
                results.add(sensorMessage)
            }
        }
        
        return results
    }
    
    /**
     * Validate sensor data format
     */
    fun isValidSensorData(rawData: String): Boolean {
        val trimmedData = rawData.trim()
        return SENSOR_DATA_PATTERN.matcher(trimmedData).matches() || 
               SIMPLE_SENSOR_PATTERN.matcher(trimmedData).matches()
    }
}