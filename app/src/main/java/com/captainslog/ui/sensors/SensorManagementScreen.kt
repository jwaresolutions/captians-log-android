package com.captainslog.ui.sensors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.bluetooth.BluetoothConnectionState
import com.captainslog.bluetooth.BluetoothDevice
import com.captainslog.viewmodel.SensorViewModel
import com.captainslog.viewmodel.SensorUiState

/**
 * Screen for managing sensor integration, Bluetooth connections, and sensor types
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: SensorViewModel = hiltViewModel()
) {
    
    val uiState by viewModel.uiState.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()
    val pairedDevices by viewModel.pairedDevices.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    val sensorTypes by viewModel.sensorTypes.collectAsState()
    
    var showAddSensorTypeDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        
        // Bluetooth Status Card
        BluetoothStatusCard(
            viewModel = viewModel,
            connectionState = connectionState,
            connectedDevice = uiState.connectedDevice,
            connectionError = uiState.connectionError,
            onClearError = { viewModel.clearConnectionError() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Device Discovery Section
        DeviceDiscoverySection(
            viewModel = viewModel,
            uiState = uiState,
            discoveredDevices = discoveredDevices,
            pairedDevices = pairedDevices,
            connectionState = connectionState
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Real-time Sensor Data Section
        if (connectionState == BluetoothConnectionState.CONNECTED) {
            SensorDataSection(
                sensorData = sensorData,
                onClearData = { viewModel.clearSensorData() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Sensor Types Section
        SensorTypesSection(
            sensorTypes = sensorTypes,
            isLoading = uiState.isLoadingSensorTypes,
            error = uiState.sensorTypesError,
            onRefresh = { viewModel.loadSensorTypes() },
            onAddSensorType = { showAddSensorTypeDialog = true },
            onClearError = { viewModel.clearSensorTypesError() }
        )
    }
    
    // Add Sensor Type Dialog
    if (showAddSensorTypeDialog) {
        AddSensorTypeDialog(
            isCreating = uiState.isCreatingSensorType,
            error = uiState.sensorTypeCreationError,
            onDismiss = { 
                showAddSensorTypeDialog = false
                viewModel.clearSensorTypeCreationError()
            },
            onCreateSensorType = { name, unit, frequency, description ->
                viewModel.createSensorType(name, unit, frequency, description)
            },
            onClearError = { viewModel.clearSensorTypeCreationError() }
        )
    }
}

@Composable
private fun BluetoothStatusCard(
    viewModel: SensorViewModel,
    connectionState: BluetoothConnectionState,
    connectedDevice: BluetoothDevice?,
    connectionError: String?,
    onClearError: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    imageVector = when (connectionState) {
                        BluetoothConnectionState.CONNECTED -> Icons.Filled.CheckCircle
                        BluetoothConnectionState.CONNECTING -> Icons.Filled.Search
                        BluetoothConnectionState.ERROR -> Icons.Filled.Warning
                        else -> Icons.Filled.Info
                    },
                    contentDescription = "Bluetooth Status",
                    tint = when (connectionState) {
                        BluetoothConnectionState.CONNECTED -> Color(0xFF4CAF50)
                        BluetoothConnectionState.ERROR -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bluetooth Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = when (connectionState) {
                            BluetoothConnectionState.CONNECTED -> "Connected to ${connectedDevice?.name ?: "Unknown Device"}"
                            BluetoothConnectionState.CONNECTING -> "Connecting..."
                            BluetoothConnectionState.DISCONNECTING -> "Disconnecting..."
                            BluetoothConnectionState.ERROR -> "Connection Error"
                            BluetoothConnectionState.DISCONNECTED -> {
                                when {
                                    !viewModel.isBluetoothSupported() -> "Bluetooth not supported"
                                    !viewModel.isBluetoothEnabled() -> "Bluetooth disabled"
                                    !viewModel.hasRequiredPermissions() -> "Missing permissions"
                                    else -> "Not connected"
                                }
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (connectionState == BluetoothConnectionState.CONNECTED) {
                    TextButton(
                        onClick = { viewModel.disconnect() }
                    ) {
                        Text("Disconnect")
                    }
                }
            }
            
            // Show connection error if present
            connectionError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onClearError) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceDiscoverySection(
    viewModel: SensorViewModel,
    uiState: SensorUiState,
    discoveredDevices: List<BluetoothDevice>,
    pairedDevices: List<BluetoothDevice>,
    connectionState: BluetoothConnectionState
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bluetooth Devices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row {
                    if (uiState.isDiscovering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = { viewModel.stopDeviceDiscovery() }
                        ) {
                            Text("Stop")
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.startDeviceDiscovery() },
                            enabled = viewModel.isBluetoothEnabled() && viewModel.hasRequiredPermissions()
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Discover Devices")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Paired Devices
            if (pairedDevices.isNotEmpty()) {
                Text(
                    text = "Paired Devices",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                pairedDevices.forEach { device ->
                    DeviceItem(
                        device = device,
                        isPaired = true,
                        isConnecting = uiState.isConnecting && connectionState == BluetoothConnectionState.CONNECTING,
                        onConnect = { viewModel.connectToDevice(device) }
                    )
                }
                
                if (discoveredDevices.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Discovered Devices
            if (discoveredDevices.isNotEmpty()) {
                Text(
                    text = "Discovered Devices",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                discoveredDevices.forEach { device ->
                    DeviceItem(
                        device = device,
                        isPaired = false,
                        isConnecting = uiState.isConnecting && connectionState == BluetoothConnectionState.CONNECTING,
                        onConnect = { viewModel.connectToDevice(device) }
                    )
                }
            }
            
            // Show message if no devices found
            if (pairedDevices.isEmpty() && discoveredDevices.isEmpty() && !uiState.isDiscovering) {
                Text(
                    text = "No devices found. Tap refresh to discover devices.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DeviceItem(
    device: BluetoothDevice,
    isPaired: Boolean,
    isConnecting: Boolean,
    onConnect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${device.address} ${if (isPaired) "(Paired)" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (isConnecting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            TextButton(onClick = onConnect) {
                Text("Connect")
            }
        }
    }
}

@Composable
private fun SensorDataSection(
    sensorData: List<com.captainslog.bluetooth.SensorData>,
    onClearData: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Real-time Sensor Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (sensorData.isNotEmpty()) {
                    TextButton(onClick = onClearData) {
                        Text("Clear")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (sensorData.isEmpty()) {
                Text(
                    text = "No sensor data received yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sensorData.takeLast(10)) { data ->
                        SensorDataItem(data = data)
                    }
                }
                
                if (sensorData.size > 10) {
                    Text(
                        text = "Showing last 10 of ${sensorData.size} readings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SensorDataItem(
    data: com.captainslog.bluetooth.SensorData
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = data.sensorType.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${data.value} ${data.unit}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date(data.timestamp)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SensorTypesSection(
    sensorTypes: List<com.captainslog.network.models.SensorTypeResponse>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onAddSensorType: () -> Unit,
    onClearError: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sensor Types",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onAddSensorType) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Sensor Type")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Show error if present
            error?.let { errorMessage ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onClearError) {
                            Text("Dismiss")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else if (sensorTypes.isEmpty()) {
                Text(
                    text = "No sensor types configured. Add sensor types to register new sensors.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sensorTypes) { sensorType ->
                        SensorTypeItem(sensorType = sensorType)
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorTypeItem(
    sensorType: com.captainslog.network.models.SensorTypeResponse
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = sensorType.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${sensorType.unit} • ${sensorType.loggingFrequency}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            sensorType.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}