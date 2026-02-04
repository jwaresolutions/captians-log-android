package com.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.data.LicenseProgress
import com.captainslog.license.LicenseCalculator
import com.captainslog.mode.AppModeManager
import com.captainslog.network.models.LicenseProgressResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for the License Progress screen.
 * Manages captain's license progress data and UI state.
 */
class LicenseProgressViewModel(
    private val context: android.content.Context
) : ViewModel() {

    private val connectionManager = com.captainslog.connection.ConnectionManager.getInstance(context)
    private val appModeManager = AppModeManager.getInstance(context)
    private val database = com.captainslog.database.AppDatabase.getInstance(context)
    private val licenseCalculator = LicenseCalculator(database.tripDao())

    data class UiState(
        val isLoading: Boolean = false,
        val progress: LicenseProgress? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Load license progress from local calculation or API
     */
    fun loadLicenseProgress() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                if (appModeManager.isStandalone()) {
                    // Use local calculation in standalone mode
                    val progress = licenseCalculator.calculateProgress()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        progress = progress,
                        error = null
                    )
                } else {
                    // Use API call in connected mode
                    val apiService = connectionManager.getApiServiceOrNull()
                    val response = apiService?.getLicenseProgress()

                    if (response != null && response.isSuccessful) {
                        val progressResponse = response.body()?.data
                        if (progressResponse != null) {
                            val progress = mapToLicenseProgress(progressResponse)
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                progress = progress,
                                error = null
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "No data received from server"
                            )
                        }
                    } else if (response != null) {
                        val errorMessage = when (response.code()) {
                            401 -> "Authentication failed. Please log in again."
                            403 -> "Access denied."
                            404 -> "License progress endpoint not found."
                            500 -> "Server error. Please try again later."
                            else -> "Failed to load license progress (${response.code()})"
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "API service not available"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("LicenseProgressVM", "Error loading license progress", e)

                val errorMessage = when {
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Request timed out. Please check your connection."
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Network error. Please check your connection."
                    else -> "Failed to load license progress: ${e.message}"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    /**
     * Refresh the license progress data
     */
    fun refresh() {
        loadLicenseProgress()
    }

    /**
     * Clear any error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Map API response to local data model
     */
    private fun mapToLicenseProgress(response: LicenseProgressResponse): LicenseProgress {
        return LicenseProgress(
            totalDays = response.totalDays,
            totalHours = response.totalHours,
            daysInLast3Years = response.daysInLast3Years,
            hoursInLast3Years = response.hoursInLast3Years,
            daysRemaining360 = response.daysRemaining360,
            daysRemaining90In3Years = response.daysRemaining90In3Years,
            estimatedCompletion360 = formatEstimatedDate(response.estimatedCompletion360),
            estimatedCompletion90In3Years = formatEstimatedDate(response.estimatedCompletion90In3Years),
            averageDaysPerMonth = response.averageDaysPerMonth
        )
    }

    /**
     * Format estimated completion date for display
     */
    private fun formatEstimatedDate(isoDate: String?): String? {
        if (isoDate == null) return null
        
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(isoDate)
            date?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            // Try simpler format
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(isoDate)
                date?.let { outputFormat.format(it) }
            } catch (e2: Exception) {
                android.util.Log.w("LicenseProgressVM", "Failed to parse date: $isoDate", e2)
                isoDate // Return original if parsing fails
            }
        }
    }
}