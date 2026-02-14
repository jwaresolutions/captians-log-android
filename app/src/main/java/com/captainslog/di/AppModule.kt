package com.captainslog.di

import android.content.Context
import com.captainslog.mode.AppModeManager
import com.captainslog.security.SecurePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecurePreferences(@ApplicationContext context: Context): SecurePreferences {
        return SecurePreferences(context)
    }

    @Provides
    @Singleton
    fun provideAppModeManager(securePreferences: SecurePreferences): AppModeManager {
        return AppModeManager(securePreferences)
    }
}
