package com.captainslog.viewmodel

import androidx.lifecycle.viewModelScope
import com.captainslog.database.entities.NoteEntity
import com.captainslog.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel for managing note data and operations.
 * Provides UI state and handles note CRUD operations with sync.
 */
@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : BaseViewModel() {

    private val _selectedNoteType = MutableStateFlow("general")
    val selectedNoteType: StateFlow<String> = _selectedNoteType

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _availableTags = MutableStateFlow<List<String>>(emptyList())
    val availableTags: StateFlow<List<String>> = _availableTags

    init {
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
            setError("Note content cannot be empty")
            return
        }

        if (type !in listOf("general", "boat", "trip")) {
            setError("Invalid note type")
            return
        }

        if (type == "boat" && boatId.isNullOrBlank()) {
            setError("Boat ID is required for boat-specific notes")
            return
        }

        if (type == "trip" && tripId.isNullOrBlank()) {
            setError("Trip ID is required for trip-specific notes")
            return
        }

        launchWithErrorHandling(
            onSuccess = {
                setSuccess("Note created successfully")
                loadAvailableTags() // Refresh tags
            }
        ) {
            val result = repository.createNote(content, type, boatId, tripId, tags)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Failed to create note")
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
            setError("Note content cannot be empty")
            return
        }

        launchWithErrorHandling(
            onSuccess = {
                setSuccess("Note updated successfully")
                if (tags != null) {
                    loadAvailableTags() // Refresh tags if they were updated
                }
            }
        ) {
            val result = repository.updateNote(noteId, content, tags)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Failed to update note")
            }
        }
    }

    /**
     * Delete a note
     */
    fun deleteNote(noteId: String) {
        launchWithErrorHandling(
            onSuccess = { setSuccess("Note deleted successfully") }
        ) {
            val result = repository.deleteNote(noteId)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Failed to delete note")
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
        launchWithErrorHandling {
            repository.syncNotesFromApi()
        }
    }

    /**
     * Sync notes to API
     */
    fun syncNotesToApi() {
        launchWithErrorHandling {
            repository.syncNotesToApi()
        }
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        clearSuccess()
    }
}
