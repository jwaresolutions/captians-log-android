package com.captainslog.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

class SecurePreferences(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "boat_tracking_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_SETUP_COMPLETE = "setup_complete"
        private const val KEY_SAVE_USERNAME = "save_username"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_PROFILE_FIRST_NAME = "profile_first_name"
        private const val KEY_PROFILE_MIDDLE_NAME = "profile_middle_name"
        private const val KEY_PROFILE_LAST_NAME = "profile_last_name"
        private const val KEY_REFERENCE_NUMBER = "reference_number"
    }

    var isSetupComplete: Boolean
        get() = sharedPreferences.getBoolean(KEY_SETUP_COMPLETE, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SETUP_COMPLETE, value).apply()

    var saveUsername: Boolean
        get() = sharedPreferences.getBoolean(KEY_SAVE_USERNAME, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SAVE_USERNAME, value).apply()

    var deviceId: String
        get() = sharedPreferences.getString(KEY_DEVICE_ID, null)
                ?: UUID.randomUUID().toString().also { deviceId = it }
        set(value) = sharedPreferences.edit().putString(KEY_DEVICE_ID, value).apply()

    var displayName: String?
        get() = sharedPreferences.getString(KEY_DISPLAY_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_DISPLAY_NAME, value).apply()

    var profileFirstName: String?
        get() = sharedPreferences.getString(KEY_PROFILE_FIRST_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_PROFILE_FIRST_NAME, value).apply()

    var profileMiddleName: String?
        get() = sharedPreferences.getString(KEY_PROFILE_MIDDLE_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_PROFILE_MIDDLE_NAME, value).apply()

    var profileLastName: String?
        get() = sharedPreferences.getString(KEY_PROFILE_LAST_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_PROFILE_LAST_NAME, value).apply()

    var referenceNumber: String?
        get() = sharedPreferences.getString(KEY_REFERENCE_NUMBER, null)
        set(value) = sharedPreferences.edit().putString(KEY_REFERENCE_NUMBER, value).apply()

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
