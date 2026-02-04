package com.captainslog.mode

import android.content.Context
import com.captainslog.security.SecurePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppModeManager private constructor(context: Context) {
    private val securePreferences = SecurePreferences(context)

    private val _currentMode = MutableStateFlow(calculateMode())
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    fun isStandalone(): Boolean = currentMode.value == AppMode.STANDALONE

    fun isConnected(): Boolean = currentMode.value == AppMode.CONNECTED

    fun isServerConfigured(): Boolean = securePreferences.remoteUrl != null

    fun hasValidToken(): Boolean {
        val token = securePreferences.jwtToken
        return token != null && token.isNotEmpty()
    }

    /**
     * Recalculate and update the current mode based on configuration state.
     * Should be called after changes to server configuration or authentication state.
     */
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

    companion object {
        @Volatile
        private var INSTANCE: AppModeManager? = null

        fun getInstance(context: Context): AppModeManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AppModeManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
