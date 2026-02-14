package com.captainslog.ui.setup

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.captainslog.security.SecurePreferences
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SetupViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var securePreferences: SecurePreferences
    private lateinit var connectionManager: com.captainslog.connection.ConnectionManager
    private lateinit var viewModel: SetupViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock SecurePreferences
        securePreferences = mockk(relaxed = true)
        every { securePreferences.jwtToken } returns null
        every { securePreferences.jwtToken = any() } just Runs
        every { securePreferences.username } returns null
        every { securePreferences.username = any() } just Runs
        every { securePreferences.remoteUrl } returns null
        every { securePreferences.remoteUrl = any() } just Runs
        every { securePreferences.remoteCertPin } returns null
        every { securePreferences.remoteCertPin = any() } just Runs
        every { securePreferences.localUrl } returns null
        every { securePreferences.localUrl = any() } just Runs
        every { securePreferences.localCertPin } returns null
        every { securePreferences.localCertPin = any() } just Runs
        every { securePreferences.isSetupComplete } returns false
        every { securePreferences.isSetupComplete = any() } just Runs

        // Mock ConnectionManager
        connectionManager = mockk(relaxed = true)
        every { connectionManager.initialize() } just Runs
        coEvery { connectionManager.testConnections() } returns Pair(false, true)

        viewModel = SetupViewModel(securePreferences, connectionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should be SERVER_CONFIG step`() {
        val state = viewModel.uiState.value
        assertEquals(SetupStep.SERVER_CONFIG, state.currentStep)
        assertEquals("", state.serverUrl)
        assertEquals("", state.certPin)
    }

    @Test
    fun `updateServerUrl should update state`() {
        val testUrl = "https://captainslog.jware.dev"
        viewModel.updateServerUrl(testUrl)
        
        val state = viewModel.uiState.value
        assertEquals(testUrl, state.serverUrl)
    }

    @Test
    fun `updateCertPin should update state`() {
        val testPin = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
        viewModel.updateCertPin(testPin)
        
        val state = viewModel.uiState.value
        assertEquals(testPin, state.certPin)
    }

    @Test
    fun `nextStep should progress from SERVER_CONFIG to TEST_CONNECTION`() {
        // Start at SERVER_CONFIG
        assertEquals(SetupStep.SERVER_CONFIG, viewModel.uiState.value.currentStep)
        
        // Move to TEST_CONNECTION
        viewModel.nextStep()
        assertEquals(SetupStep.TEST_CONNECTION, viewModel.uiState.value.currentStep)
        
        // Should stay at TEST_CONNECTION
        viewModel.nextStep()
        assertEquals(SetupStep.TEST_CONNECTION, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `previousStep should go back from TEST_CONNECTION to SERVER_CONFIG`() {
        // Navigate to TEST_CONNECTION
        viewModel.nextStep()
        assertEquals(SetupStep.TEST_CONNECTION, viewModel.uiState.value.currentStep)
        
        // Go back to SERVER_CONFIG
        viewModel.previousStep()
        assertEquals(SetupStep.SERVER_CONFIG, viewModel.uiState.value.currentStep)
        
        // Should stay at SERVER_CONFIG
        viewModel.previousStep()
        assertEquals(SetupStep.SERVER_CONFIG, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `completeSetup should mark setup as complete in preferences`() {
        viewModel.completeSetup()

        verify { securePreferences.isSetupComplete = true }
    }
}
