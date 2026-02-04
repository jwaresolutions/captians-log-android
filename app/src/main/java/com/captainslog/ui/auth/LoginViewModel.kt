package com.captainslog.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.BuildConfig
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.models.LoginRequest
import com.captainslog.security.SecurePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val serverUrl: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasStoredToken: Boolean = false,
    val saveUsername: Boolean = false
) {
    val canLogin: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && serverUrl.isNotBlank()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val securePreferences = SecurePreferences(application)
    private val connectionManager = ConnectionManager.getInstance(application)

    private val _uiState = MutableStateFlow(
        LoginUiState(
            username = if (securePreferences.saveUsername) securePreferences.username ?: "" else "",
            serverUrl = securePreferences.remoteUrl ?: BuildConfig.DEFAULT_SERVER_URL,
            hasStoredToken = securePreferences.jwtToken != null,
            saveUsername = securePreferences.saveUsername
        )
    )
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Re-read state from preferences. Called when LoginScreen appears
     * to pick up changes made by logout from a different ViewModel instance.
     */
    fun refreshState() {
        _uiState.value = LoginUiState(
            username = if (securePreferences.saveUsername) securePreferences.username ?: "" else "",
            password = "",
            serverUrl = securePreferences.remoteUrl ?: BuildConfig.DEFAULT_SERVER_URL,
            hasStoredToken = securePreferences.jwtToken != null,
            saveUsername = securePreferences.saveUsername
        )
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun updateServerUrl(serverUrl: String) {
        _uiState.update { it.copy(serverUrl = serverUrl, error = null) }
    }

    fun updateSaveUsername(save: Boolean) {
        securePreferences.saveUsername = save
        _uiState.update { it.copy(saveUsername = save) }
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Save server URL (ensure trailing slash for Retrofit) and initialize
                val url = _uiState.value.serverUrl.trimEnd('/')  + "/"
                securePreferences.remoteUrl = url
                connectionManager.initialize()

                // Get API service (without auth for login endpoint)
                val apiService = connectionManager.getApiService()

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
                    
                    Log.d("LoginViewModel", "Login successful for user: ${loginResponse.user.username}")
                    
                    _uiState.update { it.copy(isLoading = false, error = null) }
                    onSuccess()
                } else {
                    // Handle error response
                    val errorMessage = when (response.code()) {
                        401 -> "Invalid username or password"
                        400 -> "Username and password are required"
                        500 -> "Server error. Please try again later"
                        else -> "Login failed: ${response.code()}"
                    }
                    
                    Log.e("LoginViewModel", "Login failed: ${response.code()} - ${response.message()}")
                    _uiState.update { it.copy(isLoading = false, error = errorMessage) }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error", e)
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "Cannot connect to server. Check your internet connection"
                    e.message?.contains("timeout") == true -> 
                        "Connection timeout. Please try again"
                    e.message?.contains("Certificate pinning") == true -> 
                        "Server certificate verification failed"
                    else -> 
                        "Login failed: ${e.message ?: "Unknown error"}"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    fun loginOffline(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Check if we have a stored token
                val storedToken = securePreferences.jwtToken
                if (storedToken != null) {
                    // Reinitialize connection manager with stored token
                    connectionManager.initialize()
                    
                    Log.d("LoginViewModel", "Offline login successful with stored token")
                    
                    _uiState.update { it.copy(isLoading = false, error = null) }
                    onSuccess()
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "No stored session found. Please sign in online.",
                            hasStoredToken = false
                        ) 
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Offline login error", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Offline login failed: ${e.message ?: "Unknown error"}"
                    ) 
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                // Attempt to logout on server
                val apiService = connectionManager.getApiService()
                apiService.logout()
            } catch (e: Exception) {
                Log.w("LoginViewModel", "Logout API call failed, clearing local session anyway", e)
            } finally {
                // Clear local session regardless of API call result
                securePreferences.jwtToken = null
                if (!securePreferences.saveUsername) {
                    securePreferences.username = null
                }

                // Reset UI state for login screen
                _uiState.value = LoginUiState(
                    username = if (securePreferences.saveUsername) securePreferences.username ?: "" else "",
                    password = "",
                    serverUrl = securePreferences.remoteUrl ?: BuildConfig.DEFAULT_SERVER_URL,
                    hasStoredToken = false,
                    saveUsername = securePreferences.saveUsername
                )

                // Reinitialize connection manager
                connectionManager.initialize()
            }
        }
    }
}
