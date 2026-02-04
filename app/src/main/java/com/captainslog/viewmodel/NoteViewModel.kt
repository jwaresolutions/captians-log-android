package com.captainslog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.NoteEntity
import com.captainslog.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel for managing note data and operations.
 * Provides UI state and handles note CRUD operations with sync.
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    private val connectionManager: ConnectionManager

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _selectedNoteType = MutableStateFlow("general")
    val selectedNoteType: StateFlow<String> = _selectedNoteType

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _availableTags = MutableStateFlow<List<String>>(emptyList())
    val availableTags: StateFlow<List<String>> = _availableTags

    init {
        val database = AppDatabase.getDatabase(application)
        connectionManager = ConnectionManager.getInstance(application)
        connectionManager.initialize()
        
        // Initialize repository with ConnectionManager
        repository = NoteRepository(database, connectionManager)
        
        // Initial sync from API
        syncNotesFromApi()
        loadAvailableTags()
    }

    /**
     * Get all notes as a Flow
     */
    fun getAllNotes(): Flow<List<NoteEntity>> {
        return repository.getAllNotes()
    }

    /**
     * Get notes by type
     */
    fun getNotesByType(type: String): Flow<List<NoteEntity>> {
        return repository.getNotesByType(type)
    }

    /**
     * Get notes for a specific boat
     */
    fun getNotesByBoat(boatId: String): Flow<List<NoteEntity>> {
        return repository.getNotesByBoat(boatId)
    }

    /**
     * Get notes for a specific trip
     */
    fun getNotesByTrip(tripId: String): Flow<List<NoteEntity>> {
        return repository.getNotesByTrip(tripId)
    }

    /**
     * Search notes by content
     */
    fun searchNotes(query: String): Flow<List<NoteEntity>> {
        _searchQuery.value = query
        return repository.searchNotes(query)
    }

    /**
     * Get a specific note by ID
     */
    suspend fun getNoteById(noteId: String): NoteEntity? {
        return repository.getNoteById(noteId)
    }

    /**
     * Create a new note
     */
    fun createNote(
        content: String,
        type: String,
        boatId: String? = null,
        tripId: String? = null,
        tags: List<String> = emptyList()
    ) {
        if (content.isBlank()) {
            _error.value = "Note content cannot be empty"
            return
        }

        if (type !in listOf("general", "boat", "trip")) {
            _error.value = "Invalid note type"
            return
        }

        if (type == "boat" && boatId.isNullOrBlank()) {
            _error.value = "Boat ID is required for boat-specific notes"
            return
        }

        if (type == "trip" && tripId.isNullOrBlank()) {
            _error.value = "Trip ID is required for trip-specific notes"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.createNote(content, type, boatId, tripId, tags)
            
            _isLoading.value = false
            
            if (result.isSuccess) {
                _successMessage.value = "Note created successfully"
                loadAvailableTags() // Refresh tags
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to create note"
            }
        }
    }

    /**
     * Update a note
     */
    fun updateNote(
        noteId: String,
        content: String? = null,
        tags: List<String>? = null
    ) {
        if (content != null && content.isBlank()) {
            _error.value = "Note content cannot be empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.updateNote(noteId, content, tags)
            
            _isLoading.value = false
            
            if (result.isSuccess) {
                _successMessage.value = "Note updated successfully"
                if (tags != null) {
                    loadAvailableTags() // Refresh tags if they were updated
                }
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update note"
            }
        }
    }

    /**
     * Delete a note
     */
    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.deleteNote(noteId)
            
            _isLoading.value = false
            
            if (result.isSuccess) {
                _successMessage.value = "Note deleted successfully"
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to delete note"
            }
        }
    }

    /**
     * Set the selected note type filter
     */
    fun setSelectedNoteType(type: String) {
        _selectedNoteType.value = type
    }

    /**
     * Load available tags
     */
    fun loadAvailableTags() {
        viewModelScope.launch {
            val result = repository.getAllTags()
            if (result.isSuccess) {
                _availableTags.value = result.getOrNull() ?: emptyList()
            }
        }
    }

    /**
     * Sync notes from API
     */
    fun syncNotesFromApi() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.syncNotesFromApi()
            _isLoading.value = false
        }
    }

    /**
     * Sync notes to API
     */
    fun syncNotesToApi() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.syncNotesToApi()
            _isLoading.value = false
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