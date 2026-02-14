package com.captainslog

import android.app.Application
import android.util.Log
import com.captainslog.nautical.NauticalSettingsManager
import com.captainslog.nautical.tile.NauticalTilePreloader
import com.captainslog.sync.SseClient
import com.captainslog.sync.SyncNotificationHelper
import com.captainslog.sync.SyncOrchestrator
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import javax.inject.Inject

@HiltAndroidApp
class BoatTrackingApplication : Application() {

    @Inject lateinit var syncOrchestrator: SyncOrchestrator
    @Inject lateinit var syncNotificationHelper: SyncNotificationHelper
    @Inject lateinit var sseClient: SseClient
    @Inject lateinit var nauticalSettingsManager: NauticalSettingsManager

    override fun onCreate() {
        super.onCreate()

        // Initialize osmdroid configuration early
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0))
        Configuration.getInstance().userAgentValue = "CaptainsLog"

        // Create notification channel for sync notifications
        syncNotificationHelper.createNotificationChannel()

        // Schedule periodic sync
        syncOrchestrator.schedulePeriodicSync()

        // Initialize SSE client for real-time sync
        sseClient.connect()

        // Preload NOAA chart tiles in the background
        NauticalTilePreloader.preload(this, nauticalSettingsManager)
    }
}
