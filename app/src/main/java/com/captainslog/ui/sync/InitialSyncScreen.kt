package com.captainslog.ui.sync

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.captainslog.sync.SyncOrchestrator
import com.captainslog.sync.SyncProgress
import kotlinx.coroutines.launch

/**
 * Screen for managing initial sync with the server.
 * Displays progress through sync stages and handles conflict resolution.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialSyncScreen(
    syncOrchestrator: SyncOrchestrator,
    onSyncComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var syncProgress by remember { mutableStateOf<SyncProgress?>(null) }
    var isComplete by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Start sync when screen launches
    LaunchedEffect(Unit) {
        syncOrchestrator.performInitialSync().collect { progress ->
            syncProgress = progress
            if (progress.current >= progress.total && progress.total > 0) {
                if (progress.message.contains("failed", ignoreCase = true)) {
                    errorMessage = progress.message
                } else {
                    isComplete = true
                    onSyncComplete()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Initial Sync") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (!isComplete) {
                        TextButton(onClick = onCancel) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                errorMessage != null -> {
                    SyncErrorCard(
                        errorMessage = errorMessage!!,
                        onRetry = {
                            errorMessage = null
                            isComplete = false
                            scope.launch {
                                syncOrchestrator.performInitialSync().collect { progress ->
                                    syncProgress = progress
                                    if (progress.current >= progress.total && progress.total > 0) {
                                        if (progress.message.contains("failed", ignoreCase = true)) {
                                            errorMessage = progress.message
                                        } else {
                                            isComplete = true
                                            onSyncComplete()
                                        }
                                    }
                                }
                            }
                        },
                        onCancel = onCancel
                    )
                }
                isComplete -> {
                    SyncCompleteCard(onComplete = onSyncComplete)
                }
                else -> {
                    val progress = syncProgress
                    val progressFraction = if (progress != null && progress.total > 0)
                        progress.current.toFloat() / progress.total else null
                    val icon = if (progress != null && progress.message.contains("Upload", ignoreCase = true))
                        Icons.Default.CloudUpload
                    else if (progress != null && progress.message.contains("Download", ignoreCase = true))
                        Icons.Default.CloudDownload
                    else
                        Icons.Default.Sync

                    SyncStageCard(
                        icon = icon,
                        title = "Syncing Data",
                        description = progress?.message ?: "Preparing to sync...",
                        progress = progressFraction,
                        isActive = true
                    )
                }
            }
        }
    }
}

