package com.captainslog.di

import android.content.Context
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.*
import com.captainslog.database.migrations.MIGRATION_9_10
import com.captainslog.database.migrations.MIGRATION_10_11
import com.captainslog.database.migrations.MIGRATION_11_12
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "boat_tracking_database"
        )
            .addMigrations(MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()
    @Provides fun provideGpsPointDao(db: AppDatabase): GpsPointDao = db.gpsPointDao()
    @Provides fun provideBoatDao(db: AppDatabase): BoatDao = db.boatDao()
    @Provides fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
    @Provides fun provideTodoListDao(db: AppDatabase): TodoListDao = db.todoListDao()
    @Provides fun provideTodoItemDao(db: AppDatabase): TodoItemDao = db.todoItemDao()
    @Provides fun provideMarkedLocationDao(db: AppDatabase): MarkedLocationDao = db.markedLocationDao()
    @Provides fun provideCrewMemberDao(db: AppDatabase): CrewMemberDao = db.crewMemberDao()
}
