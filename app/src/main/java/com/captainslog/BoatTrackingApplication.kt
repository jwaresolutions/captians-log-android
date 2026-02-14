package com.captainslog

import android.app.Application
import com.captainslog.sync.SseClient
import com.captainslog.sync.SyncNotificationHelper
import com.captainslog.sync.SyncOrchestrator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BoatTrackingApplication : Application() {

    @Inject lateinit var syncOrchestrator: SyncOrchestrator
    @Inject lateinit var syncNotificationHelper: SyncNotificationHelper
    @Inject lateinit var sseClient: SseClient

    override fun onCreate() {
        super.onCreate()

        // Create notification channel for sync notifications
        syncNotificationHelper.createNotificationChannel()

        // Schedule periodic sync
        syncOrchestrator.schedulePeriodicSync()

        // Initialize SSE client for real-time sync
        sseClient.connect()
    }
}
