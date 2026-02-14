package com.captainslog.ui.auth

import com.captainslog.connection.ConnectionManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.LoginRequest
import com.captainslog.network.models.LoginResponse
import com.captainslog.network.models.UserResponse
import com.captainslog.security.SecurePreferences
import com.captainslog.viewmodel.TestDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var securePreferences: SecurePreferences
    private lateinit var connectionManager: ConnectionManager
    private lateinit var apiService: ApiService
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        securePreferences = mockk(relaxed = true)
        connectionManager = mockk(relaxed = true)
        apiService = mockk(relaxed = true)

        every { securePreferences.saveUsername } returns false
        every { securePreferences.username } returns null
        every { securePreferences.remoteUrl } returns null
        every { securePreferences.jwtToken } returns null
        every { connectionManager.getApiService() } returns apiService

        viewModel = LoginViewModel(securePreferences, connectionManager)
    }

    // --- initial state ---

    @Test
    fun `initial state has empty password`() {
        assertEquals("", viewModel.uiState.value.password)
    }

    @Test
    fun `initial state has no error`() {
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `initial state is not loading`() {
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has no stored token when none exists`() {
        assertFalse(viewModel.uiState.value.hasStoredToken)
    }

    @Test
    fun `initial state loads stored username when saveUsername is true`() {
        every { securePreferences.saveUsername } returns true
        every { securePreferences.username } returns "captain"

        viewModel = LoginViewModel(securePreferences, connectionManager)

        assertEquals("captain", viewModel.uiState.value.username)
    }

    // --- updateUsername ---

    @Test
    fun `updateUsername updates state and clears error`() {
        viewModel.updateUsername("newuser")

        assertEquals("newuser", viewModel.uiState.value.username)
        assertNull(viewModel.uiState.value.error)
    }

    // --- updatePassword ---

    @Test
    fun `updatePassword updates state`() {
        viewModel.updatePassword("secret")

        assertEquals("secret", viewModel.uiState.value.password)
    }

    // --- updateServerUrl ---

    @Test
    fun `updateServerUrl updates state`() {
        viewModel.updateServerUrl("https://example.com")

        assertEquals("https://example.com", viewModel.uiState.value.serverUrl)
    }

    // --- updateSaveUsername ---

    @Test
    fun `updateSaveUsername saves preference`() {
        viewModel.updateSaveUsername(true)

        verify { securePreferences.saveUsername = true }
        assertTrue(viewModel.uiState.value.saveUsername)
    }

    // --- canLogin ---

    @Test
    fun `canLogin is false with empty fields`() {
        assertFalse(viewModel.uiState.value.canLogin)
    }

    @Test
    fun `canLogin is true when all fields filled`() {
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        viewModel.updateServerUrl("https://example.com")

        assertTrue(viewModel.uiState.value.canLogin)
    }

    // --- login ---

    @Test
    fun `login success stores token and calls onSuccess`() = runTest {
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        viewModel.updateServerUrl("https://example.com")

        val loginResponse = LoginResponse(
            token = "jwt-token-123",
            user = UserResponse(username = "user", id = "1", createdAt = "", updatedAt = ""),
            expiresIn = "24h"
        )
        coEvery { apiService.login(any()) } returns Response.success(loginResponse)

        var successCalled = false
        viewModel.login { successCalled = true }

        assertTrue(successCalled)
        verify { securePreferences.jwtToken = "jwt-token-123" }
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `login failure sets error message`() = runTest {
        viewModel.updateUsername("user")
        viewModel.updatePassword("wrong")
        viewModel.updateServerUrl("https://example.com")

        coEvery { apiService.login(any()) } returns Response.error(
            401,
            okhttp3.ResponseBody.create(null, "")
        )

        var successCalled = false
        viewModel.login { successCalled = true }

        assertFalse(successCalled)
        assertEquals("Invalid username or password", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login network error sets error message`() = runTest {
        viewModel.updateUsername("user")
        viewModel.updatePassword("pass")
        viewModel.updateServerUrl("https://example.com")

        coEvery { apiService.login(any()) } throws RuntimeException("Unable to resolve host")

        viewModel.login { }

        assertNotNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.error!!.contains("Cannot connect"))
    }

    // --- loginOffline ---

    @Test
    fun `loginOffline succeeds with stored token`() = runTest {
        every { securePreferences.jwtToken } returns "stored-token"

        var successCalled = false
        viewModel.loginOffline { successCalled = true }

        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loginOffline fails without stored token`() = runTest {
        every { securePreferences.jwtToken } returns null

        var successCalled = false
        viewModel.loginOffline { successCalled = true }

        assertFalse(successCalled)
        assertNotNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.error!!.contains("No stored session"))
    }

    // --- logout ---

    @Test
    fun `logout clears token`() = runTest {
        viewModel.logout()

        verify { securePreferences.jwtToken = null }
    }

    // --- refreshState ---

    @Test
    fun `refreshState reloads from preferences`() {
        every { securePreferences.jwtToken } returns "token"

        viewModel.refreshState()

        assertTrue(viewModel.uiState.value.hasStoredToken)
    }
}
