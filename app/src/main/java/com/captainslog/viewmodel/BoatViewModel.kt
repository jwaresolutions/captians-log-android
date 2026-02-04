package com.captainslog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.BoatEntity
import com.captainslog.repository.BoatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel for managing boat data and operations.
 * Provides UI state and handles boat CRUD operations with sync.
 */
class BoatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BoatRepository
    private val connectionManager: ConnectionManager

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        val database = AppDatabase.getInstance(application)
        connectionManager = ConnectionManager.getInstance(application)
        connectionManager.initialize()
        
        // Initialize repository with ConnectionManager
        repository = BoatRepository(database, connectionManager, application)
        
        // Comprehensive sync is now handled by MainActivity on startup
        // Individual boat operations will still trigger immediate sync via repository
    }

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
            _error.value = "Boat name cannot be empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.createBoat(name)
            
            _isLoading.value = false
            
            if (result.isSuccess) {
                _successMessage.value = "Boat created successfully"
                // No need to sync again - repository already handles it
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to create boat"
            }
        }
    }

    /**
     * Toggle boat enabled status
     */
    fun toggleBoatStatus(boatId: String, enabled: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.updateBoatStatus(boatId, enabled)
            
            _isLoading.value = false
            
            if (result.isSuccess) {
                _successMessage.value = if (enabled) "Boat enabled" else "Boat disabled"
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update boat status"
            }
        }
    }

    /**
     * Set a boat as active
     */
    fun setActiveBoat(boatId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.setActiveBoat(boatId)
            
            _isLoading.value = false
            
            if (result.isSuccess) {
                _successMessage.value = "Active boat updated"
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to set active boat"
            }
        }
    }

    /**
     * Sync boats from API
     */
    fun syncBoatsFromApi() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.syncBoatsFromApi()
            _isLoading.value = false
        }
    }

    /**
     * Sync boats to API
     */
    fun syncBoatsToApi() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.syncBoatsToApi()
            _isLoading.value = false
        }
    }

    /**
     * Perform full bidirectional sync using comprehensive sync manager
     */
    fun performFullSync() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val database = (getApplication<Application>() as com.captainslog.BoatTrackingApplication).database
                val comprehensiveSyncManager = com.captainslog.sync.ComprehensiveSyncManager.getInstance(getApplication(), database)
                
                // Trigger comprehensive sync for all data types
                comprehensiveSyncManager.performFullSync()
                
                _successMessage.value = "Comprehensive sync started"
            } catch (e: Exception) {
                _error.value = "Sync failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
