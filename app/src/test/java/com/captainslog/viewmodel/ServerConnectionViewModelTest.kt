package com.captainslog.viewmodel

import com.captainslog.connection.ConnectionManager
import com.captainslog.mode.AppModeManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.LoginResponse
import com.captainslog.network.models.UserResponse
import com.captainslog.security.SecurePreferences
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ServerConnectionViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var securePreferences: SecurePreferences
    private lateinit var connectionManager: ConnectionManager
    private lateinit var appModeManager: AppModeManager
    private lateinit var apiService: ApiService
    private lateinit var viewModel: ServerConnectionViewModel

    @Before
    fun setup() {
        securePreferences = mockk(relaxed = true)
        connectionManager = mockk(relaxed = true)
        appModeManager = mockk(relaxed = true)
        apiService = mockk(relaxed = true)

        every { connectionManager.getApiService() } returns apiService

        viewModel = ServerConnectionViewModel(securePreferences, connectionManager, appModeManager)
    }

    // --- initial state ---

    @Test
    fun `initial state has empty fields`() {
        val state = viewModel.uiState.value
        assertEquals("", state.serverUrl)
        assertEquals("", state.username)
        assertEquals("", state.password)
        assertTrue(state.connectionState is ServerConnectionState.Idle)
    }

    @Test
    fun `initial state cannot connect`() {
        assertFalse(viewModel.uiState.value.canConnect)
    }

    // --- updateServerUrl ---

    @Test
    fun `updateServerUrl updates state and resets connection state`() {
        viewModel.updateServerUrl("https://api.example.com")

        assertEquals("https://api.example.com", viewModel.uiState.value.serverUrl)
        assertTrue(viewModel.uiState.value.connectionState is ServerConnectionState.Idle)
    }

    // --- updateUsername ---

    @Test
    fun `updateUsername updates state`() {
        viewModel.updateUsername("captain")

        assertEquals("captain", viewModel.uiState.value.username)
    }

    // --- updatePassword ---

    @Test
    fun `updatePassword updates state`() {
        viewModel.updatePassword("secret")

        assertEquals("secret", viewModel.uiState.value.password)
    }

    // --- canConnect ---

    @Test
    fun `canConnect is true when all fields filled`() {
        viewModel.updateServerUrl("https://example.com")
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")

        assertTrue(viewModel.uiState.value.canConnect)
    }

    @Test
    fun `canConnect is false with blank server url`() {
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")

        assertFalse(viewModel.uiState.value.canConnect)
    }

    // --- connect - URL validation ---

    @Test
    fun `connect with invalid URL sets error`() = runTest {
        viewModel.updateServerUrl("not-a-url")
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")

        viewModel.connect { }

        assertTrue(viewModel.uiState.value.connectionState is ServerConnectionState.Error)
        val error = viewModel.uiState.value.connectionState as ServerConnectionState.Error
        assertTrue(error.message.contains("Invalid URL"))
    }

    // --- connect - success ---

    @Test
    fun `connect success stores token and calls onSuccess`() = runTest {
        viewModel.updateServerUrl("https://example.com")
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")

        val loginResponse = LoginResponse(
            token = "jwt-123",
            user = UserResponse(username = "user", id = "1", createdAt = "", updatedAt = ""),
            expiresIn = "24h"
        )
        coEvery { apiService.login(any()) } returns Response.success(loginResponse)

        var successCalled = false
        viewModel.connect { successCalled = true }

        assertTrue(successCalled)
        verify { securePreferences.jwtToken = "jwt-123" }
        verify { appModeManager.refresh() }
        assertTrue(viewModel.uiState.value.connectionState is ServerConnectionState.Success)
    }

    // --- connect - auth failure ---

    @Test
    fun `connect with wrong credentials sets error`() = runTest {
        viewModel.updateServerUrl("https://example.com")
        viewModel.updateUsername("user")
        viewModel.updatePassword("wrong")

        coEvery { apiService.login(any()) } returns Response.error(
            401,
            okhttp3.ResponseBody.create(null, "")
        )

        viewModel.connect { }

        val state = viewModel.uiState.value.connectionState
        assertTrue(state is ServerConnectionState.Error)
        assertEquals("Invalid username or password", (state as ServerConnectionState.Error).message)
    }

    // --- connect - network error ---

    @Test
    fun `connect with network error sets error`() = runTest {
        viewModel.updateServerUrl("https://example.com")
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")

        coEvery { apiService.login(any()) } throws RuntimeException("Unable to resolve host")

        viewModel.connect { }

        val state = viewModel.uiState.value.connectionState
        assertTrue(state is ServerConnectionState.Error)
        assertTrue((state as ServerConnectionState.Error).message.contains("Cannot connect"))
    }

    // --- connect - initialization failure ---

    @Test
    fun `connect with initialization failure sets error`() = runTest {
        viewModel.updateServerUrl("https://example.com")
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")

        every { connectionManager.getApiService() } throws IllegalStateException("Not initialized")

        viewModel.connect { }

        val state = viewModel.uiState.value.connectionState
        assertTrue(state is ServerConnectionState.Error)
    }
}
