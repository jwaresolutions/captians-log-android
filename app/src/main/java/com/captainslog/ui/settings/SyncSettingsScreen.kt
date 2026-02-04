package com.captainslog.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

/**
 * Screen for viewing sync settings and conflict logs
 * Now focused on automatic sync status and conflict resolution
 */
@Composable
fun SyncSettingsScreen(
    conflictLogs: String,
    isSyncing: Boolean,
    onClearLogs: () -> Unit,
    onTriggerSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sync controls
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Automatic Sync Status",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Data is automatically synced immediately when you make changes and have an internet connection. Photos are uploaded only when connected to WiFi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isSyncing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Syncing pending changes...")
                    }
                } else {
                    Text(
                        text = "✓ All changes are synced",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Conflict logs
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Conflict Logs",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(
                        onClick = onClearLogs,
                        enabled = conflictLogs != "No conflicts logged"
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Clear Logs",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Text(
                    text = "When sync conflicts occur, they are resolved automatically using the newest timestamp. Details are logged here for your review.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 400.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = conflictLogs,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        }

        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About Sync",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "• All data changes sync immediately when connected\n" +
                           "• Photo metadata syncs immediately, files upload on WiFi only\n" +
                           "• When offline, changes are queued and sync when reconnected\n" +
                           "• Sync conflicts require manual resolution\n" +
                           "• Connection status is shown at the top of the app",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
