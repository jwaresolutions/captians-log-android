package com.captainslog.mode

import com.captainslog.security.SecurePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppModeManager @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    private val _currentMode = MutableStateFlow(calculateMode())
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    fun isStandalone(): Boolean = currentMode.value == AppMode.STANDALONE

    fun isConnected(): Boolean = currentMode.value == AppMode.CONNECTED

    fun isServerConfigured(): Boolean = securePreferences.remoteUrl != null

    fun hasValidToken(): Boolean {
        val token = securePreferences.jwtToken
        return token != null && token.isNotEmpty()
    }

    fun refresh() {
        _currentMode.value = calculateMode()
    }

    private fun calculateMode(): AppMode {
        return if (isServerConfigured() && hasValidToken()) {
            AppMode.CONNECTED
        } else {
            AppMode.STANDALONE
        }
    }
}
