package com.captainslog.di

import android.content.Context
import com.captainslog.database.dao.OfflineChangeDao
import com.captainslog.sync.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {
    // SyncOrchestrator, SyncStatusManager, SseClient, and all SyncHandlers
    // are provided via @Inject constructor - Hilt will automatically create them

    @Provides
    @Singleton
    fun provideOfflineChangeService(offlineChangeDao: OfflineChangeDao): OfflineChangeService {
        return OfflineChangeService(offlineChangeDao)
    }

    @Provides
    @Singleton
    fun provideSyncNotificationHelper(@ApplicationContext context: Context): SyncNotificationHelper {
        return SyncNotificationHelper(context)
    }

    @Provides
    @Singleton
    fun provideConflictLogger(@ApplicationContext context: Context): ConflictLogger {
        return ConflictLogger(context)
    }
}
