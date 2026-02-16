package com.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import com.captainslog.database.AppDatabase
import com.captainslog.nautical.NauticalSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    val database: AppDatabase,
    val nauticalSettingsManager: NauticalSettingsManager
) : ViewModel()
