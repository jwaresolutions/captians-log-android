package com.captainslog.viewmodel

import com.captainslog.database.AppDatabase
import com.captainslog.mode.AppMode
import com.captainslog.mode.AppModeManager
import com.captainslog.nautical.NauticalSettingsManager
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainNavigationViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var appModeManager: AppModeManager
    private lateinit var database: AppDatabase
    private lateinit var nauticalSettingsManager: NauticalSettingsManager
    private lateinit var viewModel: MainNavigationViewModel

    @Before
    fun setup() {
        appModeManager = mockk(relaxed = true)
        database = mockk(relaxed = true)
        nauticalSettingsManager = mockk(relaxed = true)
        every { appModeManager.currentMode } returns MutableStateFlow(AppMode.STANDALONE)
        viewModel = MainNavigationViewModel(appModeManager, database, nauticalSettingsManager)
    }

    // --- construction ---

    @Test
    fun `viewModel exposes appModeManager`() {
        assertNotNull(viewModel.appModeManager)
    }

    @Test
    fun `viewModel exposes database`() {
        assertNotNull(viewModel.database)
    }

    @Test
    fun `viewModel exposes nauticalSettingsManager`() {
        assertNotNull(viewModel.nauticalSettingsManager)
    }

    // --- app mode observation ---

    @Test
    fun `appModeManager reflects standalone mode`() {
        every { appModeManager.isStandalone() } returns true

        assertTrue(viewModel.appModeManager.isStandalone())
    }

    @Test
    fun `appModeManager reflects connected mode`() {
        every { appModeManager.isConnected() } returns true

        assertTrue(viewModel.appModeManager.isConnected())
    }

    @Test
    fun `currentMode flow reflects mode changes`() {
        val modeFlow = MutableStateFlow(AppMode.STANDALONE)
        every { appModeManager.currentMode } returns modeFlow

        viewModel = MainNavigationViewModel(appModeManager, database, nauticalSettingsManager)

        assertEquals(AppMode.STANDALONE, viewModel.appModeManager.currentMode.value)

        modeFlow.value = AppMode.CONNECTED
        assertEquals(AppMode.CONNECTED, viewModel.appModeManager.currentMode.value)
    }
}
