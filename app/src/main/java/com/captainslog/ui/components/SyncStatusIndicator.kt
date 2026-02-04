package com.captainslog.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.captainslog.BoatTrackingApplication
import com.captainslog.sync.ComprehensiveSyncManager

/**
 * A reusable component that shows sync status across the app
 * Can be used in any screen to show current sync state
 */
@Composable
fun SyncStatusIndicator(
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    onSyncClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val database = (context.applicationContext as BoatTrackingApplication).database
    val syncManager = remember { ComprehensiveSyncManager.getInstance(context, database) }
    
    val isSyncing by syncManager.isSyncing.collectAsState()
    val syncProgress by syncManager.syncProgress.collectAsState()
    val lastSyncTime by syncManager.lastSyncTime.collectAsState()
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Sync icon
        IconButton(
            onClick = { 
                if (!isSyncing) {
                    onSyncClick?.invoke() ?: syncManager.performFullSync()
                }
            },
            enabled = !isSyncing
        ) {
            when {
                isSyncing -> {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Syncing",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                lastSyncTime != null -> {
                    // Check if we actually have data to confirm sync worked
                    val database = (context.applicationContext as BoatTrackingApplication).database
                    var hasData by remember { mutableStateOf(false) }
                    
                    LaunchedEffect(lastSyncTime) {
                        hasData = database.boatDao().getAllBoatsSync().isNotEmpty()
                    }
                    
                    if (hasData) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Synced",
                            tint = Color.Green
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "No data synced",
                            tint = Color.Red
                        )
                    }
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Not synced",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Sync status text
        if (showText) {
            Column {
                when {
                    isSyncing && syncProgress != null -> {
                        Text(
                            text = syncProgress!!.message,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LinearProgressIndicator(
                            progress = { syncProgress!!.percentage / 100f },
                            modifier = Modifier.width(120.dp)
                        )
                    }
                    isSyncing -> {
                        Text(
                            text = "Syncing...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    lastSyncTime != null -> {
                        Text(
                            text = "Synced",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Green
                        )
                    }
                    else -> {
                        Text(
                            text = "Not synced",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * A compact version for use in app bars
 */
@Composable
fun CompactSyncStatusIndicator(
    modifier: Modifier = Modifier,
    onSyncClick: (() -> Unit)? = null
) {
    SyncStatusIndicator(
        modifier = modifier,
        showText = false,
        onSyncClick = onSyncClick
    )
}