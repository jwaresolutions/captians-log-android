package com.captainslog.ui.sync

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConflictResolutionSection(
    conflicts: List<SyncConflict>,
    resolvedConflicts: Set<String>,
    onResolveConflict: (SyncConflict, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Column {
                    Text(
                        text = "Sync Conflicts Detected",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "${conflicts.size} conflict${if (conflicts.size != 1) "s" else ""} need${if (conflicts.size == 1) "s" else ""} your attention",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = "The following items exist both locally and on the server with different data. " +
                       "Choose which version to keep for each conflict.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        // Conflict list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(conflicts, key = { it.entityId }) { conflict ->
                AnimatedVisibility(
                    visible = conflict.entityId !in resolvedConflicts
                ) {
                    ConflictCard(
                        conflict = conflict,
                        onResolve = { useLocal ->
                            onResolveConflict(conflict, useLocal)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConflictCard(
    conflict: SyncConflict,
    onResolve: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Conflict header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getConflictTypeName(conflict),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Timestamp comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Local",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatConflictTimestamp(conflict.localTimestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Server",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatConflictTimestamp(conflict.serverTimestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showDetails) {
                Divider()
                // Show conflict details based on type
                ConflictDetails(conflict)
            }

            // Resolution buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onResolve(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Keep Local", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Button(
                    onClick = { onResolve(false) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Keep Server", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Toggle details button
            TextButton(
                onClick = { showDetails = !showDetails },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (showDetails) "Hide Details" else "Show Details")
                Icon(
                    if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ConflictDetails(conflict: SyncConflict) {
    when (conflict) {
        is SyncConflict.BoatConflict -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ComparisonRow("Name", conflict.local.name, conflict.server.name)
                ComparisonRow(
                    "Enabled",
                    if (conflict.local.enabled) "Yes" else "No",
                    if (conflict.server.enabled) "Yes" else "No"
                )
                ComparisonRow(
                    "Active",
                    if (conflict.local.isActive) "Yes" else "No",
                    if (conflict.server.isActive) "Yes" else "No"
                )
            }
        }
        is SyncConflict.TripConflict -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ComparisonRow("Water Type", conflict.local.waterType, conflict.server.waterType)
                ComparisonRow("Role", conflict.local.role, conflict.server.role)
                ComparisonRow(
                    "Start",
                    formatConflictTimestamp(conflict.local.startTime),
                    formatConflictTimestamp(conflict.server.startTime)
                )
            }
        }
        is SyncConflict.NoteConflict -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ComparisonRow("Type", conflict.local.type, conflict.server.type)
                ComparisonRow(
                    "Content",
                    conflict.local.content.take(50) + if (conflict.local.content.length > 50) "..." else "",
                    conflict.server.content.take(50) + if (conflict.server.content.length > 50) "..." else ""
                )
            }
        }
    }
}

@Composable
fun ComparisonRow(label: String, localValue: String, serverValue: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = localValue,
                style = MaterialTheme.typography.bodySmall,
                color = if (localValue != serverValue)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = serverValue,
                style = MaterialTheme.typography.bodySmall,
                color = if (localValue != serverValue)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Get human-readable conflict type name
 */
private fun getConflictTypeName(conflict: SyncConflict): String {
    return when (conflict) {
        is SyncConflict.BoatConflict -> "Boat: ${conflict.local.name}"
        is SyncConflict.TripConflict -> "Trip: ${conflict.entityId.take(8)}"
        is SyncConflict.NoteConflict -> "Note: ${conflict.local.type}"
    }
}

/**
 * Format timestamp for conflict display
 */
private fun formatConflictTimestamp(date: java.util.Date): String {
    val formatter = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
    return formatter.format(date)
}
