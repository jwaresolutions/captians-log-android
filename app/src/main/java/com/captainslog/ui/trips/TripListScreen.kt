package com.captainslog.ui.trips

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.captainslog.database.entities.TripEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen displaying a list of all trips.
 * Shows trip summary information and allows navigation to trip details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    trips: List<TripEntity>,
    onTripClick: (String) -> Unit,
    onStartNewTrip: (String, String, String, String?, String?, Double?) -> Unit,
    boats: List<com.captainslog.database.entities.BoatEntity> = emptyList(),
    activeBoat: com.captainslog.database.entities.BoatEntity? = null,
    isTracking: Boolean = false,
    currentTrip: TripEntity? = null,
    onForceCleanup: () -> Unit = {},
    onRefreshState: () -> Unit = {},
    onNuclearStop: () -> Unit = {},
    onShareTrip: (String) -> Unit = {},
    onJoinTrip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Check if there's an active trip (trip with no end time)
    val hasActiveTrip = trips.any { it.endTime == null }
    
    // State for start trip dialog
    var showStartDialog by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Trip list content
            if (trips.isEmpty()) {
                EmptyTripList(
                    onStartNewTrip = { showStartDialog = true },
                    onJoinTrip = onJoinTrip,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Top card: either "Start New Trip" or "Active Trip"
                    item {
                        if (hasActiveTrip) {
                            // Show active trip card
                            val activeTrip = trips.first { it.endTime == null }
                            ActiveTripCard(
                                trip = activeTrip,
                                onClick = { onTripClick(activeTrip.id) }
                            )
                        } else {
                            // Show start new trip card
                            StartNewTripCard(
                                onStartTrip = { showStartDialog = true },
                                boats = boats,
                                activeBoat = activeBoat
                            )
                        }
                    }
                    
                    // Completed trips section header
                    if (trips.any { it.endTime != null }) {
                        item {
                            Text(
                                text = "Previous Trips",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Show completed trips only
                    items(
                        trips.filter { it.endTime != null },
                        key = { it.id }
                    ) { trip ->
                        TripListItem(
                            trip = trip,
                            onClick = { onTripClick(trip.id) },
                            onShare = { onShareTrip(trip.id) }
                        )
                    }
                }
            }
        }
        
        // Start Trip FAB
        FloatingActionButton(
            onClick = { showStartDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = if (hasActiveTrip)
                MaterialTheme.colorScheme.tertiary
            else
                MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (hasActiveTrip) Icons.Default.Refresh else Icons.Default.Add,
                contentDescription = if (hasActiveTrip) "View Active Trip" else "Start New Trip"
            )
        }
    }
    
    // Start Trip Dialog
    if (showStartDialog) {
        StartTripDialog(
            onDismiss = { showStartDialog = false },
            onConfirm = { boatId, waterType, role, bodyOfWater, boundaryClassification, distanceOffshore ->
                onStartNewTrip(boatId, waterType, role, bodyOfWater, boundaryClassification, distanceOffshore)
                showStartDialog = false
            },
            boats = boats,
            activeBoat = activeBoat
        )
    }
}

@Composable
fun TripListItem(
    trip: TripEntity,
    onClick: () -> Unit,
    onShare: () -> Unit = {},
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
            // Trip date and share button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTripDate(trip.startTime),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onClick)
                )

                // Share button
                IconButton(onClick = onShare) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share trip",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Show clear trip status
            when {
                trip.endTime == null -> {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Text("IN PROGRESS", fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text("COMPLETED")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Trip details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    TripDetailRow(
                        label = "Start:",
                        value = formatTime(trip.startTime)
                    )
                    trip.endTime?.let {
                        TripDetailRow(
                            label = "End:",
                            value = formatTime(it)
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    TripDetailRow(
                        label = "Water:",
                        value = trip.waterType.replaceFirstChar { it.uppercase() }
                    )
                    TripDetailRow(
                        label = "Role:",
                        value = trip.role.replaceFirstChar { it.uppercase() }
                    )
                }
            }
            
            // Duration if trip is complete
            trip.endTime?.let { endTime ->
                Spacer(modifier = Modifier.height(8.dp))
                val durationMinutes = (endTime.time - trip.startTime.time) / (1000 * 60)
                Text(
                    text = "Duration: ${formatDuration(durationMinutes)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TripDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActiveTripCard(
    trip: TripEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trip in Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Badge(
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text("Active", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Started ${formatDateTime(trip.startTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${trip.waterType.replaceFirstChar { it.uppercase() }} · ${trip.role.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap to view details",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun StartNewTripCard(
    onStartTrip: () -> Unit,
    boats: List<com.captainslog.database.entities.BoatEntity>,
    activeBoat: com.captainslog.database.entities.BoatEntity?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Ready to Start Tracking",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (activeBoat != null) {
                Text(
                    text = "Active Boat: ${activeBoat.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Text(
                    text = "Select a boat to begin tracking",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Text(
                text = "GPS tracking will start automatically",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onStartTrip,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Start New Trip",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyTripList(
    onStartNewTrip: () -> Unit,
    onJoinTrip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No trips yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start your first trip to begin tracking",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onStartNewTrip) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start New Trip")
        }
    }
}

private fun formatTripDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

private fun formatTime(date: Date): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
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
