package com.captainslog.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages license tracking preferences
 */
class LicenseTrackingPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "license_tracking_prefs", 
        Context.MODE_PRIVATE
    )

    /**
     * Whether captain's license tracking is enabled
     */
    var isLicenseTrackingEnabled: Boolean
        get() = prefs.getBoolean(KEY_LICENSE_TRACKING_ENABLED, true) // Default to enabled
        set(value) = prefs.edit().putBoolean(KEY_LICENSE_TRACKING_ENABLED, value).apply()

    companion object {
        private const val KEY_LICENSE_TRACKING_ENABLED = "license_tracking_enabled"
    }
}