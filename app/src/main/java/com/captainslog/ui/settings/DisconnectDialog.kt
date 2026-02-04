package com.captainslog.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.captainslog.sync.DisconnectResult

/**
 * Dialog for disconnecting from the server with data ownership options.
 *
 * Presents the user with:
 * - Option to download all data before disconnecting
 * - Explanation of what data will be kept vs removed
 * - Loading state during disconnect process
 * - Result summary after completion
 */
@Composable
fun DisconnectDialog(
    onDismiss: () -> Unit,
    onConfirm: (downloadFirst: Boolean) -> Unit,
    isLoading: Boolean = false,
    result: DisconnectResult? = null
) {
    var downloadFirst by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = if (result != null) "Disconnected" else "Disconnect from Server",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            when {
                result != null -> {
                    // Show result summary
                    DisconnectResultSummary(result)
                }
                isLoading -> {
                    // Show loading state
                    DisconnectLoadingState(downloadFirst)
                }
                else -> {
                    // Show confirmation UI
                    DisconnectConfirmationContent(
                        downloadFirst = downloadFirst,
                        onDownloadFirstChange = { downloadFirst = it }
                    )
                }
            }
        },
        confirmButton = {
            when {
                result != null -> {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
                isLoading -> {
                    // No button during loading
                }
                else -> {
                    TextButton(
                        onClick = { onConfirm(downloadFirst) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Disconnect")
                    }
                }
            }
        },
        dismissButton = {
            if (!isLoading && result == null) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
private fun DisconnectConfirmationContent(
    downloadFirst: Boolean,
    onDownloadFirstChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation text
        Text(
            text = "Disconnecting will clear your server credentials and remove some shared data from this device.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Download option checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = downloadFirst,
                onCheckedChange = onDownloadFirstChange
            )
            Text(
                text = "Download all data before disconnecting",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Data retention explanation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "What will happen:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                DataRetentionItem(
                    label = "Your boats",
                    description = "Kept",
                    isKept = true
                )

                DataRetentionItem(
                    label = "Other users' boats",
                    description = "Removed",
                    isKept = false
                )

                DataRetentionItem(
                    label = "Trips where you're captain",
                    description = "Kept",
                    isKept = true
                )

                DataRetentionItem(
                    label = "Trips where you're crew",
                    description = "Made read-only",
                    isKept = true
                )

                DataRetentionItem(
                    label = "Your notes and data",
                    description = "Kept",
                    isKept = true
                )

                DataRetentionItem(
                    label = "Shared data from others",
                    description = "Removed",
                    isKept = false
                )
            }
        }
    }
}

@Composable
private fun DataRetentionItem(
    label: String,
    description: String,
    isKept: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = if (isKept) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DisconnectLoadingState(downloadFirst: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()

        Text(
            text = if (downloadFirst) {
                "Downloading data and disconnecting..."
            } else {
                "Disconnecting from server..."
            },
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "This may take a moment. Please wait.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DisconnectResultSummary(result: DisconnectResult) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Successfully disconnected from server.",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Summary:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                if (result.boatsKept > 0 || result.boatsRemoved > 0) {
                    ResultSummaryItem(
                        label = "Boats",
                        kept = result.boatsKept,
                        removed = result.boatsRemoved
                    )
                }

                if (result.tripsKept > 0 || result.tripsRemoved > 0) {
                    ResultSummaryItem(
                        label = "Trips",
                        kept = result.tripsKept,
                        removed = result.tripsRemoved,
                        additional = if (result.tripsMarkedReadOnly > 0) {
                            "${result.tripsMarkedReadOnly} marked read-only"
                        } else null
                    )
                }

                if (result.notesKept > 0 || result.notesRemoved > 0) {
                    ResultSummaryItem(
                        label = "Notes",
                        kept = result.notesKept,
                        removed = result.notesRemoved
                    )
                }

                if (result.photosKept > 0 || result.photosRemoved > 0) {
                    ResultSummaryItem(
                        label = "Photos",
                        kept = result.photosKept,
                        removed = result.photosRemoved
                    )
                }

                if (result.maintenanceKept > 0 || result.maintenanceRemoved > 0) {
                    ResultSummaryItem(
                        label = "Maintenance",
                        kept = result.maintenanceKept,
                        removed = result.maintenanceRemoved
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${result.totalKept} kept, ${result.totalRemoved} removed",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "You can continue using the app in standalone mode.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ResultSummaryItem(
    label: String,
    kept: Int,
    removed: Int,
    additional: String? = null
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$kept kept, $removed removed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (additional != null) {
            Text(
                text = "  $additional",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
