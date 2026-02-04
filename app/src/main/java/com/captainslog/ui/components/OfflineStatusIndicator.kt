package com.captainslog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.captainslog.sync.OfflineStatus

/**
 * Composable that shows offline status and queued action indicators
 */
@Composable
fun OfflineStatusIndicator(
    offlineStatus: OfflineStatus,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    if (!offlineStatus.hasPendingChanges && isConnected) {
        // No offline status to show when connected and no pending changes
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                offlineStatus.failedCount > 0 -> MaterialTheme.colorScheme.errorContainer
                offlineStatus.hasPendingChanges && !isConnected -> MaterialTheme.colorScheme.warningContainer
                offlineStatus.hasPendingChanges && isConnected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = when {
                        offlineStatus.failedCount > 0 -> Icons.Default.Warning
                        !isConnected -> Icons.Default.Warning // Use Warning for offline
                        else -> Icons.Default.Warning // Use Warning for sync pending
                    },
                    contentDescription = null,
                    tint = when {
                        offlineStatus.failedCount > 0 -> MaterialTheme.colorScheme.onErrorContainer
                        offlineStatus.hasPendingChanges && !isConnected -> MaterialTheme.colorScheme.onWarningContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = getStatusTitle(offlineStatus, isConnected),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            offlineStatus.failedCount > 0 -> MaterialTheme.colorScheme.onErrorContainer
                            offlineStatus.hasPendingChanges && !isConnected -> MaterialTheme.colorScheme.onWarningContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )

                    if (offlineStatus.hasPendingChanges) {
                        Text(
                            text = getStatusMessage(offlineStatus),
                            fontSize = 12.sp,
                            color = when {
                                offlineStatus.failedCount > 0 -> MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                                offlineStatus.hasPendingChanges && !isConnected -> MaterialTheme.colorScheme.onWarningContainer.copy(alpha = 0.8f)
                                else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            }
                        )
                    }
                }
            }

            // Show syncing indicator when data is being synced
            if (isConnected && offlineStatus.hasPendingChanges) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = when {
                            offlineStatus.failedCount > 0 -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Syncing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            offlineStatus.failedCount > 0 -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
        }
    }
}

/**
 * Get the appropriate status title based on offline status and connectivity
 */
private fun getStatusTitle(offlineStatus: OfflineStatus, isConnected: Boolean): String {
    return when {
        offlineStatus.failedCount > 0 -> "Sync Errors"
        offlineStatus.hasPendingChanges && !isConnected -> "No Internet Connection"
        offlineStatus.hasPendingChanges && isConnected -> "Syncing Changes"
        else -> "All Synced"
    }
}

/**
 * Get the appropriate status message based on offline status
 */
private fun getStatusMessage(offlineStatus: OfflineStatus): String {
    return buildString {
        if (offlineStatus.pendingCount > 0) {
            append("${offlineStatus.pendingCount} change")
            if (offlineStatus.pendingCount > 1) append("s")
            append(" will sync when connected")
        }
        
        if (offlineStatus.failedCount > 0) {
            if (isNotEmpty()) append(", ")
            append("${offlineStatus.failedCount} failed")
        }
        
        if (isEmpty()) {
            append("Changes sync automatically")
        }
    }
}

/**
 * Extension property to get warning container color
 */
private val ColorScheme.warningContainer: Color
    get() = Color(0xFFFFF3CD) // Light yellow/orange

/**
 * Extension property to get warning container content color
 */
private val ColorScheme.onWarningContainer: Color
    get() = Color(0xFF856404) // Dark yellow/brown