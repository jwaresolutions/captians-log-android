package com.captainslog.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.connection.ConnectionManager
import com.captainslog.security.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SetupStep {
    SERVER_CONFIG,
    TEST_CONNECTION
}

data class ConnectionTestResult(
    val success: Boolean,
    val message: String = ""
)

data class SetupUiState(
    val currentStep: SetupStep = SetupStep.SERVER_CONFIG,
    val serverUrl: String = "",
    val certPin: String = "",
    val isTesting: Boolean = false,
    val testResult: ConnectionTestResult? = null
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val connectionManager: ConnectionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun updateServerUrl(url: String) {
        _uiState.update { it.copy(serverUrl = url) }
    }

    fun updateCertPin(pin: String) {
        _uiState.update { it.copy(certPin = pin) }
    }

    fun nextStep() {
        _uiState.update {
            val nextStep = when (it.currentStep) {
                SetupStep.SERVER_CONFIG -> SetupStep.TEST_CONNECTION
                SetupStep.TEST_CONNECTION -> SetupStep.TEST_CONNECTION
            }
            it.copy(currentStep = nextStep)
        }
    }

    fun previousStep() {
        _uiState.update {
            val prevStep = when (it.currentStep) {
                SetupStep.SERVER_CONFIG -> SetupStep.SERVER_CONFIG
                SetupStep.TEST_CONNECTION -> SetupStep.SERVER_CONFIG
            }
            it.copy(currentStep = prevStep)
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            // Save configuration temporarily to test
            val state = _uiState.value
            securePreferences.remoteUrl = state.serverUrl
            securePreferences.remoteCertPin = state.certPin

            // Clear local server configuration (will be set in advanced settings later)
            securePreferences.localUrl = null
            securePreferences.localCertPin = null

            // Initialize connection manager with new config (no JWT token yet)
            connectionManager.initialize()

            // Update UI to show testing state
            _uiState.update {
                it.copy(
                    isTesting = true,
                    testResult = null
                )
            }

            try {
                // Test connection (only remote since local is not configured)
                val (_, remoteSuccess) = connectionManager.testConnections()

                _uiState.update {
                    it.copy(
                        isTesting = false,
                        testResult = ConnectionTestResult(
                            success = remoteSuccess,
                            message = if (remoteSuccess)
                                "Server is reachable. You can now log in."
                            else
                                "Could not connect to server. Check URL${if (com.captainslog.BuildConfig.REQUIRE_CERT_PINNING) " and certificate pin" else ""}."
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isTesting = false,
                        testResult = ConnectionTestResult(
                            success = false,
                            message = "Connection test failed: ${e.message}"
                        )
                    )
                }
            }
        }
    }

    fun completeSetup() {
        // Configuration is already saved during testConnections
        // Just mark setup as complete
        securePreferences.isSetupComplete = true
    }
}
