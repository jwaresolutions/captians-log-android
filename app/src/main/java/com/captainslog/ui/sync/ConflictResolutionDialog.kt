package com.captainslog.ui.sync

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.NoteEntity
import com.captainslog.database.entities.TripEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Sealed class hierarchy for representing different types of sync conflicts
 */
sealed class SyncConflict {
    abstract val entityId: String
    abstract val localTimestamp: Date
    abstract val serverTimestamp: Date

    data class BoatConflict(
        override val entityId: String,
        val local: BoatEntity,
        val server: BoatEntity,
        override val localTimestamp: Date,
        override val serverTimestamp: Date
    ) : SyncConflict()

    data class TripConflict(
        override val entityId: String,
        val local: TripEntity,
        val server: TripEntity,
        override val localTimestamp: Date,
        override val serverTimestamp: Date
    ) : SyncConflict()

    data class NoteConflict(
        override val entityId: String,
        val local: NoteEntity,
        val server: NoteEntity,
        override val localTimestamp: Date,
        override val serverTimestamp: Date
    ) : SyncConflict()
}

/**
 * Dialog for resolving sync conflicts between local and server data.
 * Presents user with local vs server versions and resolution options.
 */
@Composable
fun ConflictResolutionDialog(
    conflict: SyncConflict,
    onKeepLocal: () -> Unit,
    onKeepServer: () -> Unit,
    onKeepBoth: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Sync Conflict",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getEntityTypeName(conflict),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Explanation
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = "This ${getEntityTypeName(conflict).lowercase()} has been modified both " +
                               "locally and on the server. Choose which version to keep.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Divider()

                // Data comparison
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Local version
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Local Version",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = formatTimestamp(conflict.localTimestamp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Text(
                                    text = getLocalDataSummary(conflict),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Server version
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "Server Version",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                Text(
                                    text = formatTimestamp(conflict.serverTimestamp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Text(
                                    text = getServerDataSummary(conflict),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                Divider()

                // Resolution options
                Text(
                    text = "Resolution Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Keep Local button
                OutlinedButton(
                    onClick = onKeepLocal,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Keep Local",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Overwrite server with your local changes",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Keep Server button
                OutlinedButton(
                    onClick = onKeepServer,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Keep Server",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Discard local changes and use server version",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Keep Both button
                OutlinedButton(
                    onClick = onKeepBoth,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        Icons.Default.MergeType,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Keep Both",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Create a duplicate to preserve both versions",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Divider()

                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Decide Later")
                }
            }
        }
    }
}

/**
 * Get human-readable entity type name
 */
private fun getEntityTypeName(conflict: SyncConflict): String {
    return when (conflict) {
        is SyncConflict.BoatConflict -> "Boat"
        is SyncConflict.TripConflict -> "Trip"
        is SyncConflict.NoteConflict -> "Note"
    }
}

/**
 * Get summary of local data for display
 */
private fun getLocalDataSummary(conflict: SyncConflict): String {
    return when (conflict) {
        is SyncConflict.BoatConflict -> {
            buildString {
                appendLine("Name: ${conflict.local.name}")
                appendLine("Status: ${if (conflict.local.enabled) "Enabled" else "Disabled"}")
                appendLine("Active: ${if (conflict.local.isActive) "Yes" else "No"}")
            }
        }
        is SyncConflict.TripConflict -> {
            buildString {
                appendLine("Start: ${formatTimestamp(conflict.local.startTime)}")
                if (conflict.local.endTime != null) {
                    appendLine("End: ${formatTimestamp(conflict.local.endTime)}")
                } else {
                    appendLine("Status: In Progress")
                }
                appendLine("Water: ${conflict.local.waterType}")
                appendLine("Role: ${conflict.local.role}")
            }
        }
        is SyncConflict.NoteConflict -> {
            buildString {
                appendLine("Type: ${conflict.local.type}")
                appendLine("Content: ${conflict.local.content.take(50)}${if (conflict.local.content.length > 50) "..." else ""}")
                if (conflict.local.tags.isNotEmpty()) {
                    appendLine("Tags: ${conflict.local.tags.joinToString(", ")}")
                }
            }
        }
    }
}

/**
 * Get summary of server data for display
 */
private fun getServerDataSummary(conflict: SyncConflict): String {
    return when (conflict) {
        is SyncConflict.BoatConflict -> {
            buildString {
                appendLine("Name: ${conflict.server.name}")
                appendLine("Status: ${if (conflict.server.enabled) "Enabled" else "Disabled"}")
                appendLine("Active: ${if (conflict.server.isActive) "Yes" else "No"}")
            }
        }
        is SyncConflict.TripConflict -> {
            buildString {
                appendLine("Start: ${formatTimestamp(conflict.server.startTime)}")
                if (conflict.server.endTime != null) {
                    appendLine("End: ${formatTimestamp(conflict.server.endTime)}")
                } else {
                    appendLine("Status: In Progress")
                }
                appendLine("Water: ${conflict.server.waterType}")
                appendLine("Role: ${conflict.server.role}")
            }
        }
        is SyncConflict.NoteConflict -> {
            buildString {
                appendLine("Type: ${conflict.server.type}")
                appendLine("Content: ${conflict.server.content.take(50)}${if (conflict.server.content.length > 50) "..." else ""}")
                if (conflict.server.tags.isNotEmpty()) {
                    appendLine("Tags: ${conflict.server.tags.joinToString(", ")}")
                }
            }
        }
    }
}

/**
 * Format timestamp for display
 */
private fun formatTimestamp(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}
