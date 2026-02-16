package com.captainslog.di

import android.content.Context
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.*
import com.captainslog.repository.*
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
        database: AppDatabase
    ): BoatRepository {
        return BoatRepository(database)
    }

    @Provides
    @Singleton
    fun provideTripRepository(
        database: AppDatabase
    ): TripRepository {
        return TripRepository(database)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        database: AppDatabase
    ): NoteRepository {
        return NoteRepository(database)
    }

    @Provides
    @Singleton
    fun provideTodoRepository(
        todoListDao: TodoListDao,
        todoItemDao: TodoItemDao
    ): TodoRepository {
        return TodoRepository(todoListDao, todoItemDao)
    }

    @Provides
    @Singleton
    fun provideMarkedLocationRepository(
        database: AppDatabase
    ): MarkedLocationRepository {
        return MarkedLocationRepository(database)
    }
}
