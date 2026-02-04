package com.captainslog.nautical

import android.content.Context
import android.content.SharedPreferences
import com.captainslog.nautical.model.NauticalProviderConfig
import com.captainslog.nautical.model.NauticalSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NauticalSettingsManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "nautical_settings"
        private const val KEY_SETTINGS = "settings_json"

        @Volatile
        private var instance: NauticalSettingsManager? = null

        fun getInstance(context: Context): NauticalSettingsManager {
            return instance ?: synchronized(this) {
                instance ?: NauticalSettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<NauticalSettings> = _settings.asStateFlow()

    private fun loadSettings(): NauticalSettings {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, NauticalProviderConfig>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun persist(settings: NauticalSettings) {
        prefs.edit().putString(KEY_SETTINGS, gson.toJson(settings)).apply()
        _settings.value = settings
    }

    fun getProviderConfig(id: String): NauticalProviderConfig {
        return _settings.value[id] ?: NauticalProviderConfig()
    }

    fun isEnabled(id: String): Boolean {
        return _settings.value[id]?.enabled ?: false
    }

    fun toggleProvider(id: String) {
        val current = _settings.value.toMutableMap()
        val config = current[id] ?: NauticalProviderConfig()
        current[id] = config.copy(enabled = !config.enabled)
        persist(current)
    }

    fun setApiKey(id: String, apiKey: String) {
        val current = _settings.value.toMutableMap()
        val config = current[id] ?: NauticalProviderConfig()
        current[id] = config.copy(apiKey = apiKey)
        persist(current)
    }

    fun setProviderOption(id: String, key: String, value: String) {
        val current = _settings.value.toMutableMap()
        val config = current[id] ?: NauticalProviderConfig()
        current[id] = config.copy(options = config.options + (key to value))
        persist(current)
    }
}
