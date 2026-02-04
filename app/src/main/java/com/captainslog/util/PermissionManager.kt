package com.captainslog.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Utility class for managing runtime permissions.
 * Handles requesting and checking permissions required for the app.
 */
object PermissionManager {
    
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002
    const val ALL_PERMISSIONS_REQUEST_CODE = 1003
    const val BLUETOOTH_PERMISSION_REQUEST_CODE = 1004

    /**
     * All permissions required for the app to function properly
     */
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * Permissions required for Android 13+ (API 33+)
     */
    val ANDROID_13_PERMISSIONS = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS
    )

    /**
     * Bluetooth permissions required for Android 12+ (API 31+)
     * Note: BLUETOOTH and BLUETOOTH_ADMIN are normal permissions (auto-granted) on all versions.
     * BLUETOOTH_CONNECT and BLUETOOTH_SCAN are runtime permissions only on Android 12+.
     */
    val BLUETOOTH_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        emptyArray()
    }
    
    /**
     * Check if all required permissions are granted
     */
    fun hasAllRequiredPermissions(context: Context): Boolean {
        val requiredPermissions = REQUIRED_PERMISSIONS.toMutableList()
        
        // Add notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.addAll(ANDROID_13_PERMISSIONS)
        }
        
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if notification permission is granted (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not required on older versions
        }
    }

    /**
     * Check if Bluetooth permissions are granted
     * On Android 12+ (API 31+): checks BLUETOOTH_CONNECT and BLUETOOTH_SCAN
     * On older versions: returns true (BLUETOOTH and BLUETOOTH_ADMIN are normal permissions)
     */
    fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            BLUETOOTH_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            true // BLUETOOTH and BLUETOOTH_ADMIN are normal permissions on older versions
        }
    }
    
    /**
     * Get list of permissions that are not granted
     */
    fun getMissingPermissions(context: Context): List<String> {
        val requiredPermissions = REQUIRED_PERMISSIONS.toMutableList()
        
        // Add notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.addAll(ANDROID_13_PERMISSIONS)
        }
        
        return requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Request all required permissions
     */
    fun requestAllPermissions(activity: Activity) {
        val missingPermissions = getMissingPermissions(activity)
        
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                missingPermissions.toTypedArray(),
                ALL_PERMISSIONS_REQUEST_CODE
            )
        }
    }
    
    /**
     * Request location permissions specifically
     */
    fun requestLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            REQUIRED_PERMISSIONS,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    
    /**
     * Request notification permission (Android 13+)
     */
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                ANDROID_13_PERMISSIONS,
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Request Bluetooth permissions (Android 12+)
     */
    fun requestBluetoothPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                activity,
                BLUETOOTH_PERMISSIONS,
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        }
    }
    
    /**
     * Check if we should show rationale for a permission
     */
    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
    
    /**
     * Get user-friendly permission names for display
     */
    fun getPermissionDisplayName(permission: String): String {
        return when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> "Precise Location"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "Approximate Location"
            Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
            Manifest.permission.BLUETOOTH_CONNECT -> "Bluetooth Connect"
            Manifest.permission.BLUETOOTH_SCAN -> "Bluetooth Scan"
            else -> permission.substringAfterLast(".")
        }
    }

    /**
     * Get permission explanation for user
     */
    fun getPermissionExplanation(permission: String): String {
        return when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION ->
                "Required for GPS tracking during trips. This allows the app to record your boat's location and create trip routes."
            Manifest.permission.ACCESS_COARSE_LOCATION ->
                "Required for basic location services and GPS tracking functionality."
            Manifest.permission.POST_NOTIFICATIONS ->
                "Required to show notifications when GPS tracking is active during trips."
            Manifest.permission.BLUETOOTH_CONNECT ->
                "Required to connect to Bluetooth sensor devices like Arduino modules for boat monitoring."
            Manifest.permission.BLUETOOTH_SCAN ->
                "Required to discover nearby Bluetooth sensor devices for your boat."
            else -> "Required for app functionality."
        }
    }
}