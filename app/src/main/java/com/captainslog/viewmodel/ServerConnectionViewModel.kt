package com.captainslog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.connection.ConnectionManager
import com.captainslog.mode.AppModeManager
import com.captainslog.network.models.LoginRequest
import com.captainslog.security.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ServerConnectionState {
    data object Idle : ServerConnectionState()
    data object Loading : ServerConnectionState()
    data object Success : ServerConnectionState()
    data class Error(val message: String) : ServerConnectionState()
}

data class ServerConnectionUiState(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val connectionState: ServerConnectionState = ServerConnectionState.Idle
) {
    val canConnect: Boolean
        get() = serverUrl.isNotBlank() &&
                username.isNotBlank() &&
                password.isNotBlank() &&
                connectionState !is ServerConnectionState.Loading

    val isLoading: Boolean
        get() = connectionState is ServerConnectionState.Loading
}

@HiltViewModel
class ServerConnectionViewModel @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val connectionManager: ConnectionManager,
    private val appModeManager: AppModeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServerConnectionUiState())
    val uiState: StateFlow<ServerConnectionUiState> = _uiState.asStateFlow()

    fun updateServerUrl(url: String) {
        _uiState.update { it.copy(serverUrl = url, connectionState = ServerConnectionState.Idle) }
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, connectionState = ServerConnectionState.Idle) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, connectionState = ServerConnectionState.Idle) }
    }

    fun connect(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(connectionState = ServerConnectionState.Loading) }

            try {
                // Validate URL format
                val url = _uiState.value.serverUrl.trim()
                if (!isValidUrl(url)) {
                    _uiState.update {
                        it.copy(connectionState = ServerConnectionState.Error("Invalid URL format. Use https://example.com"))
                    }
                    return@launch
                }

                // Save server URL (ensure trailing slash for Retrofit) and initialize
                val normalizedUrl = url.trimEnd('/') + "/"
                securePreferences.remoteUrl = normalizedUrl
                connectionManager.initialize()

                // Get API service (should be initialized after setting URL)
                val apiService = try {
                    connectionManager.getApiService()
                } catch (e: IllegalStateException) {
                    _uiState.update {
                        it.copy(connectionState = ServerConnectionState.Error("Failed to initialize connection"))
                    }
                    return@launch
                }

                // Attempt login
                val response = apiService.login(
                    LoginRequest(
                        username = _uiState.value.username,
                        password = _uiState.value.password
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    // Store JWT token and username
                    securePreferences.jwtToken = loginResponse.token
                    securePreferences.username = loginResponse.user.username

                    // Reinitialize connection manager with new token
                    connectionManager.initialize()

                    // Refresh app mode
                    appModeManager.refresh()

                    Log.d("ServerConnectionVM", "Connection successful for user: ${loginResponse.user.username}")

                    _uiState.update { it.copy(connectionState = ServerConnectionState.Success) }
                    onSuccess()
                } else {
                    // Handle error response
                    val errorMessage = when (response.code()) {
                        401 -> "Invalid username or password"
                        400 -> "Username and password are required"
                        404 -> "Server not found. Check the URL"
                        500 -> "Server error. Please try again later"
                        else -> "Connection failed: ${response.code()}"
                    }

                    Log.e("ServerConnectionVM", "Connection failed: ${response.code()} - ${response.message()}")
                    _uiState.update { it.copy(connectionState = ServerConnectionState.Error(errorMessage)) }
                }
            } catch (e: Exception) {
                Log.e("ServerConnectionVM", "Connection error", e)
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "Cannot connect to server. Check your internet connection and URL"
                    e.message?.contains("timeout") == true ->
                        "Connection timeout. Check your internet connection"
                    e.message?.contains("Certificate pinning") == true ->
                        "Server certificate verification failed"
                    e.message?.contains("Failed to connect") == true ->
                        "Cannot reach server. Check the URL and your network"
                    else ->
                        "Connection failed: ${e.message ?: "Unknown error"}"
                }
                _uiState.update { it.copy(connectionState = ServerConnectionState.Error(errorMessage)) }
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            val trimmedUrl = url.trim()
            // Basic validation: must start with http:// or https://
            trimmedUrl.startsWith("http://", ignoreCase = true) ||
            trimmedUrl.startsWith("https://", ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }
}
