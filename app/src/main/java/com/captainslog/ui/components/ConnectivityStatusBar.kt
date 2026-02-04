package com.captainslog.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.captainslog.network.NetworkMonitor
import com.captainslog.sync.OfflineStatus

/**
 * Status bar that appears at the top of the app to show connectivity and sync status
 */
@Composable
fun ConnectivityStatusBar(
    isConnected: Boolean,
    isServerReachable: Boolean,
    connectionType: NetworkMonitor.ConnectionType,
    offlineStatus: OfflineStatus,
    isSyncing: Boolean,
    hasUnresolvedConflicts: Boolean,
    onSyncConflictClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Only show the bar when there's something important to display
    val shouldShow = !isConnected || (isConnected && !isServerReachable) || offlineStatus.hasPendingChanges || isSyncing || hasUnresolvedConflicts
    
    AnimatedVisibility(
        visible = shouldShow,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = getStatusBarColor(isConnected, isServerReachable, offlineStatus, hasUnresolvedConflicts),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status icon and text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = getStatusIcon(isConnected, isServerReachable, connectionType, isSyncing, hasUnresolvedConflicts),
                        contentDescription = null,
                        tint = getStatusTextColor(isConnected, isServerReachable, offlineStatus, hasUnresolvedConflicts),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column {
                        Text(
                            text = getStatusTitle(isConnected, isServerReachable, connectionType, isSyncing, hasUnresolvedConflicts),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = getStatusTextColor(isConnected, isServerReachable, offlineStatus, hasUnresolvedConflicts)
                        )

                        if (offlineStatus.hasPendingChanges || hasUnresolvedConflicts) {
                            Text(
                                text = getStatusSubtitle(offlineStatus, hasUnresolvedConflicts),
                                fontSize = 12.sp,
                                color = getStatusTextColor(isConnected, isServerReachable, offlineStatus, hasUnresolvedConflicts).copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                // Action button for conflicts
                if (hasUnresolvedConflicts && isConnected) {
                    TextButton(
                        onClick = onSyncConflictClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = getStatusTextColor(isConnected, isServerReachable, offlineStatus, hasUnresolvedConflicts)
                        )
                    ) {
                        Text(
                            text = "Resolve",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Syncing indicator
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = getStatusTextColor(isConnected, isServerReachable, offlineStatus, hasUnresolvedConflicts)
                    )
                }
            }
        }
    }
}

/**
 * Get the appropriate background color for the status bar
 */
private fun getStatusBarColor(
    isConnected: Boolean,
    isServerReachable: Boolean,
    offlineStatus: OfflineStatus,
    hasUnresolvedConflicts: Boolean
): Color {
    return when {
        hasUnresolvedConflicts -> Color(0xFFFFEBEE) // Light red
        !isConnected -> Color(0xFFFFF3E0) // Light orange
        !isServerReachable -> Color(0xFFFFF8E1) // Light amber
        offlineStatus.hasPendingChanges -> Color(0xFFE3F2FD) // Light blue
        else -> Color(0xFFE8F5E8) // Light green
    }
}

/**
 * Get the appropriate text color for the status bar
 */
private fun getStatusTextColor(
    isConnected: Boolean,
    isServerReachable: Boolean,
    offlineStatus: OfflineStatus,
    hasUnresolvedConflicts: Boolean
): Color {
    return when {
        hasUnresolvedConflicts -> Color(0xFFD32F2F) // Red
        !isConnected -> Color(0xFFE65100) // Orange
        !isServerReachable -> Color(0xFFF57F17) // Amber
        offlineStatus.hasPendingChanges -> Color(0xFF1976D2) // Blue
        else -> Color(0xFF388E3C) // Green
    }
}

/**
 * Get the appropriate icon for the current status
 */
private fun getStatusIcon(
    isConnected: Boolean,
    isServerReachable: Boolean,
    connectionType: NetworkMonitor.ConnectionType,
    isSyncing: Boolean,
    hasUnresolvedConflicts: Boolean
) = when {
    hasUnresolvedConflicts -> Icons.Default.Warning
    !isConnected -> Icons.Default.CloudOff
    !isServerReachable -> Icons.Default.CloudOff
    isSyncing -> Icons.Default.Sync
    connectionType == NetworkMonitor.ConnectionType.WIFI -> Icons.Default.Wifi
    connectionType == NetworkMonitor.ConnectionType.MOBILE_DATA -> Icons.Default.SignalCellular4Bar
    else -> Icons.Default.Wifi
}

/**
 * Get the main status title text
 */
private fun getStatusTitle(
    isConnected: Boolean,
    isServerReachable: Boolean,
    connectionType: NetworkMonitor.ConnectionType,
    isSyncing: Boolean,
    hasUnresolvedConflicts: Boolean
): String {
    return when {
        hasUnresolvedConflicts -> "Sync Conflicts"
        !isConnected -> "No Internet Connection"
        !isServerReachable -> "Cannot Reach Server"
        isSyncing -> "Syncing..."
        connectionType == NetworkMonitor.ConnectionType.WIFI -> "Connected via WiFi"
        connectionType == NetworkMonitor.ConnectionType.MOBILE_DATA -> "Connected via Mobile Data"
        else -> "Connected"
    }
}

/**
 * Get the subtitle text for additional information
 */
private fun getStatusSubtitle(
    offlineStatus: OfflineStatus,
    hasUnresolvedConflicts: Boolean
): String {
    return when {
        hasUnresolvedConflicts -> "Tap Resolve to handle conflicts"
        offlineStatus.hasPendingChanges -> {
            buildString {
                append("${offlineStatus.pendingCount} change")
                if (offlineStatus.pendingCount > 1) append("s")
                append(" will sync when connected")
                if (offlineStatus.failedCount > 0) {
                    append(" (${offlineStatus.failedCount} failed)")
                }
            }
        }
        else -> ""
    }
}