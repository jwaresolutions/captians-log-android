package com.captainslog.viewmodel

import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.TripDao
import com.captainslog.mode.AppModeManager
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LicenseProgressViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var connectionManager: ConnectionManager
    private lateinit var appModeManager: AppModeManager
    private lateinit var database: AppDatabase
    private lateinit var tripDao: TripDao
    private lateinit var viewModel: LicenseProgressViewModel

    @Before
    fun setup() {
        connectionManager = mockk(relaxed = true)
        appModeManager = mockk(relaxed = true)
        database = mockk(relaxed = true)
        tripDao = mockk(relaxed = true)
        every { database.tripDao() } returns tripDao
        viewModel = LicenseProgressViewModel(connectionManager, appModeManager, database)
    }

    // --- initial state ---

    @Test
    fun `initial state is not loading`() {
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has no progress`() {
        assertNull(viewModel.uiState.value.progress)
    }

    @Test
    fun `initial state has no error`() {
        assertNull(viewModel.uiState.value.error)
    }

    // --- loadLicenseProgress standalone ---

    @Test
    fun `loadLicenseProgress in standalone mode calculates locally`() = runTest {
        every { appModeManager.isStandalone() } returns true
        every { tripDao.getAllTrips() } returns flowOf(emptyList())

        viewModel.loadLicenseProgress()

        assertFalse(viewModel.uiState.value.isLoading)
        // Progress should be set (even if zero values)
        assertNotNull(viewModel.uiState.value.progress)
        assertNull(viewModel.uiState.value.error)
    }

    // --- loadLicenseProgress connected - no API service ---

    @Test
    fun `loadLicenseProgress with null API service sets error`() = runTest {
        every { appModeManager.isStandalone() } returns false
        every { connectionManager.getApiServiceOrNull() } returns null

        viewModel.loadLicenseProgress()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("API service not available", viewModel.uiState.value.error)
    }

    // --- loadLicenseProgress error ---

    @Test
    fun `loadLicenseProgress exception sets error`() = runTest {
        every { appModeManager.isStandalone() } returns true
        every { tripDao.getAllTrips() } throws RuntimeException("DB error")

        viewModel.loadLicenseProgress()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.error)
    }

    // --- clearError ---

    @Test
    fun `clearError clears error state`() {
        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }

    // --- refresh ---

    @Test
    fun `refresh delegates to loadLicenseProgress`() = runTest {
        every { appModeManager.isStandalone() } returns true
        every { tripDao.getAllTrips() } returns flowOf(emptyList())

        viewModel.refresh()

        assertFalse(viewModel.uiState.value.isLoading)
    }
}
