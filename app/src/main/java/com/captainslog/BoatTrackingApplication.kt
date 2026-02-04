package com.captainslog

import android.app.Application
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.sync.SseClient
import com.captainslog.sync.SyncManager
import com.captainslog.sync.SyncNotificationHelper

class BoatTrackingApplication : Application() {
    lateinit var database: AppDatabase
        private set

    lateinit var connectionManager: ConnectionManager
        private set

    lateinit var syncManager: SyncManager
        private set

    lateinit var sseClient: SseClient
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize database
        database = AppDatabase.getDatabase(this)
        
        // Initialize connection manager
        connectionManager = ConnectionManager.getInstance(this)
        connectionManager.initialize()

        // Initialize sync manager
        syncManager = SyncManager.getInstance(this)
        
        // Create notification channel for sync notifications
        SyncNotificationHelper(this).createNotificationChannel()
        
        // Schedule periodic sync
        syncManager.schedulePeriodicSync()

        // Initialize SSE client for real-time sync
        sseClient = SseClient.getInstance(this, database)
        sseClient.connect()
    }
}
