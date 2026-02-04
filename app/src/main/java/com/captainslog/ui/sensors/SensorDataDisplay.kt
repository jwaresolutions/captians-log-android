package com.captainslog.ui.sensors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.captainslog.bluetooth.BluetoothConnectionState
import com.captainslog.bluetooth.SensorData
import java.text.SimpleDateFormat
import java.util.*

/**
 * Component for displaying real-time sensor data during trips
 */
@Composable
fun SensorDataDisplay(
    connectionState: BluetoothConnectionState,
    sensorData: List<SensorData>,
    modifier: Modifier = Modifier,
    maxItems: Int = 5
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (connectionState) {
                BluetoothConnectionState.CONNECTED -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                BluetoothConnectionState.ERROR -> Color(0xFFF44336).copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Sensor Data",
                    tint = when (connectionState) {
                        BluetoothConnectionState.CONNECTED -> Color(0xFF4CAF50)
                        BluetoothConnectionState.ERROR -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sensor Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = when (connectionState) {
                            BluetoothConnectionState.CONNECTED -> "Connected - ${sensorData.size} readings"
                            BluetoothConnectionState.CONNECTING -> "Connecting..."
                            BluetoothConnectionState.DISCONNECTING -> "Disconnecting..."
                            BluetoothConnectionState.ERROR -> "Connection Error"
                            BluetoothConnectionState.DISCONNECTED -> "Not connected"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (connectionState == BluetoothConnectionState.CONNECTED) {
                Spacer(modifier = Modifier.height(12.dp))
                
                if (sensorData.isEmpty()) {
                    Text(
                        text = "No sensor data received yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // Show latest sensor readings
                    val recentData = sensorData.takeLast(maxItems)
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        recentData.forEach { data ->
                            SensorReadingItem(data = data)
                        }
                    }
                    
                    if (sensorData.size > maxItems) {
                        Text(
                            text = "Showing latest $maxItems of ${sensorData.size} readings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorReadingItem(
    data: SensorData
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = data.sensorType.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
        
        Text(
            text = "${data.value} ${data.unit}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(2f)
        )
        
        Text(
            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(Date(data.timestamp)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Compact version for use in trip screens
 */
@Composable
fun CompactSensorDataDisplay(
    connectionState: BluetoothConnectionState,
    sensorData: List<SensorData>,
    modifier: Modifier = Modifier
) {
    if (connectionState == BluetoothConnectionState.CONNECTED && sensorData.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Sensors",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    Text(
                        text = "Live Sensors (${sensorData.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Show latest 3 readings in a compact format
                val latestData = sensorData.takeLast(3)
                latestData.forEach { data ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = data.sensorType.uppercase(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${data.value} ${data.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}