package com.captainslog.nautical.alert

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.captainslog.nautical.service.NoaaWeatherService
import java.util.concurrent.TimeUnit

class MarineAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "MarineAlertWorker"
        private const val CHANNEL_ID = "marine_weather_alerts"
        private const val WORK_NAME = "marine_alert_check"
        private const val PREFS_NAME = "marine_alert_prefs"
        private const val KEY_SEEN_IDS = "seen_alert_ids"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<MarineAlertWorker>(15, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        try {
            val prefs = applicationContext.getSharedPreferences("captains_log_prefs", Context.MODE_PRIVATE)
            val lat = prefs.getFloat("last_map_lat", Float.NaN)
            val lon = prefs.getFloat("last_map_lon", Float.NaN)
            if (lat.isNaN() || lon.isNaN()) return Result.success()

            val alerts = NoaaWeatherService.fetchAlerts(lat.toDouble(), lon.toDouble())
            if (alerts.isEmpty()) return Result.success()

            val alertPrefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val seenIds = alertPrefs.getStringSet(KEY_SEEN_IDS, emptySet()) ?: emptySet()
            val newAlerts = alerts.filter { it.id !in seenIds }

            if (newAlerts.isEmpty()) return Result.success()

            // Save seen IDs
            val updatedIds = seenIds + newAlerts.map { it.id }
            alertPrefs.edit().putStringSet(KEY_SEEN_IDS, updatedIds).apply()

            // Create notification channel
            createNotificationChannel()

            // Show notifications for new alerts
            newAlerts.forEach { alert ->
                showNotification(alert.id.hashCode(), alert.event, alert.headline, alert.severity)
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking marine alerts", e)
            return Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Marine Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for active marine weather alerts"
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(id: Int, title: String, body: String, severity: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val priority = when (severity.lowercase()) {
            "extreme", "severe" -> NotificationCompat.PRIORITY_HIGH
            "moderate" -> NotificationCompat.PRIORITY_DEFAULT
            else -> NotificationCompat.PRIORITY_LOW
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(priority)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(id, notification)
    }
}
