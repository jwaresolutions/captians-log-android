package com.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import com.captainslog.database.AppDatabase
import com.captainslog.mode.AppModeManager
import com.captainslog.nautical.NauticalSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for MainNavigation
 * Provides access to app mode manager, database, and nautical settings
 */
@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    val appModeManager: AppModeManager,
    val database: AppDatabase,
    val nauticalSettingsManager: NauticalSettingsManager
) : ViewModel()
