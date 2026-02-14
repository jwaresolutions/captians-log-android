package com.captainslog.ui.trips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.captainslog.bluetooth.BluetoothConnectionState
import com.captainslog.bluetooth.SensorData
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.repository.TripStatistics
import com.captainslog.ui.sensors.CompactSensorDataDisplay
import com.captainslog.ui.components.DetailRow
import com.captainslog.ui.components.StatisticRow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen displaying detailed information about a specific trip.
 * Shows trip statistics, GPS route information, and manual data.
 */
@Composable
fun TripDetailScreen(
    trip: TripEntity,
    gpsPoints: List<GpsPointEntity>,
    statistics: TripStatistics?,
    boats: List<com.captainslog.database.entities.BoatEntity> = emptyList(),
    onNavigateBack: () -> Unit,
    onStopTrip: () -> Unit = {},
    onUpdateManualData: (TripEntity) -> Unit = {},
    // Optional sensor data parameters
    sensorConnectionState: BluetoothConnectionState = BluetoothConnectionState.DISCONNECTED,
    sensorData: List<SensorData> = emptyList(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            // Trip header with edit functionality
            TripHeaderCard(
                trip = trip,
                boats = boats,
                onEditTrip = { updatedTrip ->
                    onUpdateManualData(updatedTrip)
                }
            )
            
            // ALWAYS show stop button at the top for visibility
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (trip.endTime == null) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (trip.endTime == null) {
                        // ACTIVE TRIP - Prominent stop button
                        Text(
                            text = "🔴 TRIP IN PROGRESS",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Text(
                            text = "GPS tracking is active - tap to stop",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // HUGE STOP BUTTON
                        Button(
                            onClick = {
                                android.util.Log.d("TripDetailScreen", "🛑 STOP TRIP button clicked for active trip ${trip.id}")
                                onStopTrip()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = androidx.compose.ui.graphics.Color.White
                            )
                        ) {
                            Text(
                                "🛑 STOP TRIP",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                    } else {
                        // COMPLETED TRIP
                        Text(
                            text = "✅ TRIP COMPLETED",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "Ended: ${formatDateTime(trip.endTime!!)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            // Sensor data display (only for active trips)
            if (trip.endTime == null) {
                CompactSensorDataDisplay(
                    connectionState = sensorConnectionState,
                    sensorData = sensorData
                )
            }
            
            // Statistics card
            if (statistics != null) {
                TripStatisticsCard(statistics = statistics)
            }
            
            // GPS information
            GpsInformationCard(gpsPoints = gpsPoints)
            
            // Manual data card with edit functionality
            ManualDataCard(
                trip = trip,
                onEditManualData = onUpdateManualData
            )
            
            // Photo capture component
            com.captainslog.ui.components.PhotoCaptureComponent(
                entityType = "trip",
                entityId = trip.id
            )
            
            // Sync status
            SyncStatusCard(trip = trip)
        }
}

@Composable
fun TripHeaderCard(
    trip: TripEntity,
    boats: List<com.captainslog.database.entities.BoatEntity>,
    onEditTrip: (TripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = formatTripDate(trip.startTime),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    // Edit button (only for completed trips)
                    if (trip.endTime != null) {
                        Button(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Edit", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    
                    // Status badge
                    Badge(
                        containerColor = if (trip.endTime == null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    ) {
                        Text(
                            text = if (trip.endTime == null) "IN PROGRESS" else "COMPLETED",
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow(label = "Start Time", value = formatDateTime(trip.startTime))
            trip.endTime?.let {
                DetailRow(label = "End Time", value = formatDateTime(it))
            }
            DetailRow(label = "Water Type", value = trip.waterType.replaceFirstChar { it.uppercase() })
            DetailRow(label = "Role", value = trip.role.replaceFirstChar { it.uppercase() })
            
            // Show boat name if available
            val boatName = boats.find { it.id == trip.boatId }?.name ?: trip.boatId
            DetailRow(label = "Boat", value = boatName)
        }
    }
    
    if (showEditDialog) {
        TripEditDialog(
            trip = trip,
            boats = boats,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTrip ->
                showEditDialog = false
                onEditTrip(updatedTrip)
            }
        )
    }
}

@Composable
fun TripStatisticsCard(
    statistics: TripStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            StatisticRow(
                label = "Duration",
                value = formatDuration(statistics.durationSeconds / 60)
            )
            StatisticRow(
                label = "Distance",
                value = String.format("%.2f nm", statistics.distanceMeters / 1852.0)
            )
            StatisticRow(
                label = "Average Speed",
                value = String.format("%.1f knots", statistics.averageSpeedKnots)
            )
            StatisticRow(
                label = "Max Speed",
                value = String.format("%.1f knots", statistics.maxSpeedKnots)
            )
        }
    }
}

@Composable
fun GpsInformationCard(
    gpsPoints: List<GpsPointEntity>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "GPS Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow(
                label = "GPS Points",
                value = gpsPoints.size.toString()
            )
            
            if (gpsPoints.isNotEmpty()) {
                val firstPoint = gpsPoints.first()
                val lastPoint = gpsPoints.last()
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Start Position",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                DetailRow(
                    label = "Latitude",
                    value = String.format("%.6f", firstPoint.latitude)
                )
                DetailRow(
                    label = "Longitude",
                    value = String.format("%.6f", firstPoint.longitude)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "End Position",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                DetailRow(
                    label = "Latitude",
                    value = String.format("%.6f", lastPoint.latitude)
                )
                DetailRow(
                    label = "Longitude",
                    value = String.format("%.6f", lastPoint.longitude)
                )
            }
        }
    }
}

@Composable
fun ManualDataCard(
    trip: TripEntity,
    onEditManualData: (TripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "Manual Data",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(if (hasManualData(trip)) "Edit" else "Add")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (hasManualData(trip)) {
                trip.engineHours?.let {
                    DetailRow(label = "Engine Hours", value = String.format("%.1f hrs", it))
                }
                trip.fuelConsumed?.let {
                    DetailRow(label = "Fuel Consumed", value = String.format("%.1f gal", it))
                }
                trip.weatherConditions?.let {
                    DetailRow(label = "Weather", value = it)
                }
                trip.numberOfPassengers?.let {
                    DetailRow(label = "Passengers", value = it.toString())
                }
                trip.destination?.let {
                    DetailRow(label = "Destination", value = it)
                }
            } else {
                Text(
                    text = "No manual data entered",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    if (showEditDialog) {
        ManualDataEditDialog(
            trip = trip,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTrip ->
                showEditDialog = false
                onEditManualData(updatedTrip)
            }
        )
    }
}

@Composable
fun SyncStatusCard(
    trip: TripEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trip.synced) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Sync Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (trip.synced) "Synced" else "Not Synced",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun hasManualData(trip: TripEntity): Boolean {
    return trip.engineHours != null ||
            trip.fuelConsumed != null ||
            trip.weatherConditions != null ||
            trip.numberOfPassengers != null ||
            trip.destination != null
}

private fun formatTripDate(date: Date): String {
    val formatter = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

private fun formatDateTime(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}

private fun formatDuration(minutes: Long): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours > 0 -> "${hours}h ${mins}m"
        else -> "${mins}m"
    }
}

