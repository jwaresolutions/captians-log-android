package com.captainslog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.data.LicenseProgress
import com.captainslog.database.AppDatabase
import com.captainslog.license.LicenseCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the License Progress screen.
 * Manages captain's license progress data and UI state.
 */
@HiltViewModel
class LicenseProgressViewModel @Inject constructor(
    database: AppDatabase
) : ViewModel() {

    private val licenseCalculator = LicenseCalculator(database.tripDao())

    data class UiState(
        val isLoading: Boolean = false,
        val progress: LicenseProgress? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadLicenseProgress() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val progress = licenseCalculator.calculateProgress()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    progress = progress,
                    error = null
                )
            } catch (e: Exception) {
                Log.e("LicenseProgressVM", "Error loading license progress", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load license progress: ${e.message}"
                )
            }
        }
    }

    fun refresh() {
        loadLicenseProgress()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
