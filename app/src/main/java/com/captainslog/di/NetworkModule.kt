package com.captainslog.di

import android.content.Context
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // ConnectionManager and NetworkMonitor are now provided via @Inject constructor
    // No need for @Provides methods - Hilt will automatically create them
}
