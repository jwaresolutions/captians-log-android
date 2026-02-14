package com.captainslog.di

import android.content.Context
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.*
import com.captainslog.repository.*
import com.captainslog.sync.SyncOrchestrator
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBoatRepository(
        database: AppDatabase,
        connectionManager: ConnectionManager,
        @ApplicationContext context: Context,
        syncOrchestrator: Lazy<SyncOrchestrator>
    ): BoatRepository {
        return BoatRepository(database, connectionManager, context, syncOrchestrator)
    }

    @Provides
    @Singleton
    fun provideTripRepository(
        database: AppDatabase,
        @ApplicationContext context: Context,
        connectionManager: ConnectionManager,
        syncOrchestrator: Lazy<SyncOrchestrator>
    ): TripRepository {
        return TripRepository(database, context, connectionManager, syncOrchestrator)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        database: AppDatabase,
        connectionManager: ConnectionManager
    ): NoteRepository {
        return NoteRepository(database, connectionManager)
    }

    @Provides
    @Singleton
    fun provideTodoRepository(
        connectionManager: ConnectionManager,
        todoListDao: TodoListDao,
        todoItemDao: TodoItemDao
    ): TodoRepository {
        return TodoRepository(connectionManager, todoListDao, todoItemDao)
    }

    @Provides
    @Singleton
    fun providePhotoRepository(
        database: AppDatabase,
        @ApplicationContext context: Context,
        connectionManager: ConnectionManager,
        syncOrchestrator: Lazy<SyncOrchestrator>
    ): PhotoRepository {
        return PhotoRepository(database, context, connectionManager, syncOrchestrator)
    }

    @Provides
    @Singleton
    fun provideMarkedLocationRepository(
        database: AppDatabase,
        connectionManager: ConnectionManager
    ): MarkedLocationRepository {
        return MarkedLocationRepository(database, connectionManager)
    }

    @Provides
    @Singleton
    fun provideSensorRepository(
        connectionManager: ConnectionManager
    ): SensorRepository {
        return SensorRepository(connectionManager)
    }

    @Provides
    @Singleton
    fun provideMaintenanceTemplateRepository(
        connectionManager: ConnectionManager,
        templateDao: MaintenanceTemplateDao,
        eventDao: MaintenanceEventDao,
        offlineChangeDao: OfflineChangeDao,
        syncOrchestrator: Lazy<SyncOrchestrator>
    ): MaintenanceTemplateRepository {
        return MaintenanceTemplateRepository(connectionManager, templateDao, eventDao, offlineChangeDao, syncOrchestrator)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        connectionManager: ConnectionManager
    ): NotificationRepository {
        return NotificationRepository(connectionManager)
    }
}
