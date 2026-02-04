package com.captainslog.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.captainslog.R

/**
 * Helper for showing sync-related notifications
 */
class SyncNotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "sync_notifications"
        private const val CHANNEL_NAME = "Sync Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications about data synchronization and conflicts"
    }

    /**
     * Create notification channel (required for Android O+)
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show a notification about a sync conflict
     */
    fun showConflictNotification(
        tripId: String,
        conflictMessage: String
    ) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Trip Sync Conflict")
            .setContentText(conflictMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(conflictMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(tripId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Permission not granted, skip notification
            android.util.Log.w("SyncNotificationHelper", "Notification permission not granted")
        }
    }

    /**
     * Show a notification about successful sync
     */
    fun showSyncSuccessNotification(
        syncedCount: Int
    ) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Sync Complete")
            .setContentText("Successfully synced $syncedCount trip(s)")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(1001, notification)
        } catch (e: SecurityException) {
            // Permission not granted, skip notification
            android.util.Log.w("SyncNotificationHelper", "Notification permission not granted")
        }
    }

    /**
     * Show a notification about sync failure
     */
    fun showSyncFailureNotification(
        errorMessage: String
    ) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Sync Failed")
            .setContentText(errorMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(errorMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(1002, notification)
        } catch (e: SecurityException) {
            // Permission not granted, skip notification
            android.util.Log.w("SyncNotificationHelper", "Notification permission not granted")
        }
    }

    /**
     * Show a notification about template sync results
     */
    fun showSyncResult(
        successCount: Int,
        failureCount: Int,
        conflictCount: Int
    ) {
        createNotificationChannel()

        val title = when {
            conflictCount > 0 -> "Sync Conflicts Detected"
            failureCount > 0 -> "Sync Partially Failed"
            successCount > 0 -> "Sync Complete"
            else -> "No Changes to Sync"
        }

        val message = buildString {
            if (successCount > 0) append("$successCount synced")
            if (failureCount > 0) {
                if (isNotEmpty()) append(", ")
                append("$failureCount failed")
            }
            if (conflictCount > 0) {
                if (isNotEmpty()) append(", ")
                append("$conflictCount conflicts")
            }
            if (isEmpty()) append("No changes found")
        }

        val icon = when {
            conflictCount > 0 -> android.R.drawable.ic_dialog_alert
            failureCount > 0 -> android.R.drawable.ic_dialog_alert
            else -> android.R.drawable.ic_dialog_info
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(1003, notification)
        } catch (e: SecurityException) {
            android.util.Log.w("SyncNotificationHelper", "Notification permission not granted")
        }
    }

    /**
     * Show a notification about template conflicts
     */
    fun showTemplateConflictNotification(
        templateId: String,
        conflictMessage: String
    ) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Template Sync Conflict")
            .setContentText(conflictMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(conflictMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(templateId.hashCode(), notification)
        } catch (e: SecurityException) {
            android.util.Log.w("SyncNotificationHelper", "Notification permission not granted")
        }
    }
}
