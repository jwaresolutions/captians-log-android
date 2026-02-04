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
    private lateinit var application: Application
    private lateinit var viewModel: SetupViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock application context
        application = mockk(relaxed = true)
        
        // Mock SecurePreferences
        mockkConstructor(SecurePreferences::class)
        every { anyConstructed<SecurePreferences>().jwtToken } returns null
        every { anyConstructed<SecurePreferences>().jwtToken = any() } just Runs
        every { anyConstructed<SecurePreferences>().username } returns null
        every { anyConstructed<SecurePreferences>().username = any() } just Runs
        every { anyConstructed<SecurePreferences>().remoteUrl } returns null
        every { anyConstructed<SecurePreferences>().remoteUrl = any() } just Runs
        every { anyConstructed<SecurePreferences>().remoteCertPin } returns null
        every { anyConstructed<SecurePreferences>().remoteCertPin = any() } just Runs
        every { anyConstructed<SecurePreferences>().localUrl } returns null
        every { anyConstructed<SecurePreferences>().localUrl = any() } just Runs
        every { anyConstructed<SecurePreferences>().localCertPin } returns null
        every { anyConstructed<SecurePreferences>().localCertPin = any() } just Runs
        every { anyConstructed<SecurePreferences>().isSetupComplete } returns false
        every { anyConstructed<SecurePreferences>().isSetupComplete = any() } just Runs

        viewModel = SetupViewModel(application)
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
        
        verify { anyConstructed<SecurePreferences>().isSetupComplete = true }
    }
}
