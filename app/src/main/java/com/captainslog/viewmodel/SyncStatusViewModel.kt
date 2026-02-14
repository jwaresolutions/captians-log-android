package com.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import com.captainslog.sync.SyncOrchestrator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel for sync status indicator
 * Provides access to sync orchestrator for UI components
 */
@HiltViewModel
class SyncStatusViewModel @Inject constructor(
    val syncOrchestrator: SyncOrchestrator
) : ViewModel() {

    val isSyncing: StateFlow<Boolean> = syncOrchestrator.isSyncing
    val syncProgress = syncOrchestrator.syncProgress
    val lastSyncTime = syncOrchestrator.lastSyncTime

    fun performFullSync() {
        syncOrchestrator.syncAll()
    }
}
