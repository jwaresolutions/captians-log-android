package com.captainslog.viewmodel

import androidx.lifecycle.viewModelScope
import com.captainslog.database.entities.BoatEntity
import com.captainslog.repository.BoatRepository
import com.captainslog.sync.SyncOrchestrator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel for managing boat data and operations.
 * Provides UI state and handles boat CRUD operations with sync.
 */
@HiltViewModel
class BoatViewModel @Inject constructor(
    private val repository: BoatRepository,
    private val syncOrchestrator: SyncOrchestrator
) : BaseViewModel() {

    /**
     * Get all boats as a Flow
     */
    fun getAllBoats(): Flow<List<BoatEntity>> {
        return repository.getAllBoats()
    }

    /**
     * Get the active boat
     */
    suspend fun getActiveBoat(): BoatEntity? {
        return repository.getActiveBoat()
    }

    /**
     * Create a new boat
     */
    fun createBoat(name: String) {
        if (name.isBlank()) {
            setError("Boat name cannot be empty")
            return
        }

        launchWithErrorHandling(
            onSuccess = { setSuccess("Boat created successfully") }
        ) {
            val result = repository.createBoat(name)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Failed to create boat")
            }
        }
    }

    /**
     * Update boat details (name, vessel details, owner info)
     */
    fun updateBoatDetails(boat: BoatEntity) {
        launchWithErrorHandling(
            onSuccess = { setSuccess("Boat updated successfully") }
        ) {
            val result = repository.updateBoatDetails(boat)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Failed to update boat")
            }
        }
    }

    /**
     * Toggle boat enabled status
     */
    fun toggleBoatStatus(boatId: String, enabled: Boolean) {
        launchWithErrorHandling(
            onSuccess = { setSuccess(if (enabled) "Boat enabled" else "Boat disabled") }
        ) {
            val result = repository.updateBoatStatus(boatId, enabled)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Failed to update boat status")
            }
        }
    }

    /**
     * Set a boat as active
     */
    fun setActiveBoat(boatId: String) {
        launchWithErrorHandling(
            onSuccess = { setSuccess("Active boat updated") }
        ) {
            val result = repository.setActiveBoat(boatId)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Failed to set active boat")
            }
        }
    }

    /**
     * Sync boats from API
     */
    fun syncBoatsFromApi() {
        launchWithErrorHandling {
            repository.syncBoatsFromApi()
        }
    }

    /**
     * Sync boats to API
     */
    fun syncBoatsToApi() {
        launchWithErrorHandling {
            repository.syncBoatsToApi()
        }
    }

    /**
     * Perform full bidirectional sync using comprehensive sync manager
     */
    fun performFullSync() {
        launchWithErrorHandling(
            onSuccess = { setSuccess("Comprehensive sync started") }
        ) {
            syncOrchestrator.syncAll()
        }
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        clearSuccess()
    }
}
