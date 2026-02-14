package com.captainslog.viewmodel

import com.captainslog.sync.SyncOrchestrator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncStatusViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var syncOrchestrator: SyncOrchestrator
    private lateinit var viewModel: SyncStatusViewModel

    @Before
    fun setup() {
        syncOrchestrator = mockk(relaxed = true)
        every { syncOrchestrator.isSyncing } returns MutableStateFlow(false)
        every { syncOrchestrator.syncProgress } returns MutableStateFlow(null)
        every { syncOrchestrator.lastSyncTime } returns MutableStateFlow(null)
        viewModel = SyncStatusViewModel(syncOrchestrator)
    }

    // --- initial state ---

    @Test
    fun `initial isSyncing is false`() {
        assertFalse(viewModel.isSyncing.value)
    }

    @Test
    fun `initial syncProgress is null`() {
        assertNull(viewModel.syncProgress.value)
    }

    @Test
    fun `initial lastSyncTime is null`() {
        assertNull(viewModel.lastSyncTime.value)
    }

    // --- isSyncing reflects orchestrator ---

    @Test
    fun `isSyncing reflects orchestrator state`() {
        val syncingFlow = MutableStateFlow(false)
        every { syncOrchestrator.isSyncing } returns syncingFlow
        viewModel = SyncStatusViewModel(syncOrchestrator)

        assertFalse(viewModel.isSyncing.value)

        syncingFlow.value = true
        assertTrue(viewModel.isSyncing.value)
    }

    // --- lastSyncTime reflects orchestrator ---

    @Test
    fun `lastSyncTime reflects orchestrator state`() {
        val timeFlow = MutableStateFlow<Long?>(null)
        every { syncOrchestrator.lastSyncTime } returns timeFlow
        viewModel = SyncStatusViewModel(syncOrchestrator)

        assertNull(viewModel.lastSyncTime.value)

        timeFlow.value = 1234567890L
        assertEquals(1234567890L, viewModel.lastSyncTime.value)
    }

    // --- performFullSync ---

    @Test
    fun `performFullSync calls syncOrchestrator syncAll`() {
        viewModel.performFullSync()

        verify { syncOrchestrator.syncAll() }
    }
}
