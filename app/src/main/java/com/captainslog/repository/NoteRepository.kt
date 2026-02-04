package com.captainslog.repository

import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.NoteEntity
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.CreateNoteRequest
import com.captainslog.network.models.UpdateNoteRequest
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository for managing note data.
 * Handles both local database operations and API synchronization.
 */
class NoteRepository(
    private val database: AppDatabase,
    private val connectionManager: ConnectionManager
) {

    /**
     * Get all notes as a Flow for reactive updates
     */
    fun getAllNotes(): Flow<List<NoteEntity>> {
        return database.noteDao().getAllNotes()
    }

    /**
     * Get notes by type
     */
    fun getNotesByType(type: String): Flow<List<NoteEntity>> {
        return database.noteDao().getNotesByType(type)
    }

    /**
     * Get notes for a specific boat
     */
    fun getNotesByBoat(boatId: String): Flow<List<NoteEntity>> {
        return database.noteDao().getNotesByBoat(boatId)
    }

    /**
     * Get notes for a specific trip
     */
    fun getNotesByTrip(tripId: String): Flow<List<NoteEntity>> {
        return database.noteDao().getNotesByTrip(tripId)
    }

    /**
     * Search notes by content
     */
    fun searchNotes(query: String): Flow<List<NoteEntity>> {
        return database.noteDao().searchNotes(query)
    }

    /**
     * Get a specific note by ID
     */
    suspend fun getNoteById(noteId: String): NoteEntity? {
        return database.noteDao().getNoteById(noteId)
    }

    /**
     * Create a new note locally and sync to API
     */
    suspend fun createNote(
        content: String,
        type: String,
        boatId: String? = null,
        tripId: String? = null,
        tags: List<String> = emptyList()
    ): Result<NoteEntity> {
        return try {
            // Create note locally first
            val note = NoteEntity(
                content = content,
                type = type,
                boatId = boatId,
                tripId = tripId,
                tags = tags,
                synced = false,
                lastModified = Date(),
                createdAt = Date()
            )
            
            database.noteDao().insertNote(note)
            
            // Try to sync to API
            try {
                val request = CreateNoteRequest(
                    content = content,
                    type = type,
                    boatId = boatId,
                    tripId = tripId,
                    tags = tags
                )
                val apiService = connectionManager.getApiService()
                val response = apiService.createNote(request)
                if (response.isSuccessful && response.body() != null) {
                    val apiNote = response.body()!!.data
                    // Update local note with server ID and mark as synced
                    val syncedNote = note.copy(
                        id = apiNote.id,
                        synced = true
                    )
                    database.noteDao().insertNote(syncedNote)
                    Result.success(syncedNote)
                } else {
                    // API call failed, but note is saved locally
                    Result.success(note)
                }
            } catch (e: Exception) {
                // Network error, but note is saved locally
                Result.success(note)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update a note
     */
    suspend fun updateNote(
        noteId: String,
        content: String? = null,
        tags: List<String>? = null
    ): Result<NoteEntity> {
        return try {
            val existingNote = database.noteDao().getNoteById(noteId)
            if (existingNote != null) {
                val updatedNote = existingNote.copy(
                    content = content ?: existingNote.content,
                    tags = tags ?: existingNote.tags,
                    synced = false,
                    lastModified = Date()
                )
                database.noteDao().updateNote(updatedNote)
                
                // Try to sync to API
                try {
                    val request = UpdateNoteRequest(
                        content = content,
                        tags = tags
                    )
                    val apiService = connectionManager.getApiService()
                    apiService.updateNote(noteId, request)
                    database.noteDao().markAsSynced(noteId)
                } catch (e: Exception) {
                    // Network error, will sync later
                }
                
                Result.success(updatedNote)
            } else {
                Result.failure(Exception("Note not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a note
     */
    suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            // Try to delete from API first
            try {
                val apiService = connectionManager.getApiService()
                apiService.deleteNote(noteId)
            } catch (e: Exception) {
                // Network error, continue with local deletion
            }
            
            // Delete from local database
            database.noteDao().deleteNoteById(noteId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all unique tags
     */
    suspend fun getAllTags(): Result<List<String>> {
        return try {
            // Try to get from API first
            try {
                val apiService = connectionManager.getApiService()
                val response = apiService.getAllTags()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.data)
                } else {
                    // Fallback to local tags
                    val localTags = database.noteDao().getAllTags().flatMap { tagString ->
                        if (tagString.isEmpty()) emptyList() else tagString.split(",")
                    }.distinct()
                    Result.success(localTags)
                }
            } catch (e: Exception) {
                // Network error, use local tags
                val localTags = database.noteDao().getAllTags().flatMap { tagString ->
                    if (tagString.isEmpty()) emptyList() else tagString.split(",")
                }.distinct()
                Result.success(localTags)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync notes from API to local database
     */
    suspend fun syncNotesFromApi(): Result<Unit> {
        return try {
            val apiService = connectionManager.getApiService()
            val response = apiService.getNotes()
            if (response.isSuccessful && response.body() != null) {
                val apiNotes = response.body()!!.data
                val localNotes = apiNotes.map { apiNote ->
                    NoteEntity(
                        id = apiNote.id,
                        content = apiNote.content,
                        type = apiNote.type,
                        boatId = apiNote.boatId,
                        tripId = apiNote.tripId,
                        tags = apiNote.tags,
                        synced = true,
                        lastModified = Date(),
                        createdAt = Date()
                    )
                }
                database.noteDao().insertNotes(localNotes)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch notes from API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync unsynced notes to API
     */
    suspend fun syncNotesToApi(): Result<Unit> {
        return try {
            val apiService = connectionManager.getApiService()
            val unsyncedNotes = database.noteDao().getUnsyncedNotes()
            for (note in unsyncedNotes) {
                try {
                    // Try to update or create on server
                    val response = apiService.getNote(note.id)
                    if (response.isSuccessful) {
                        // Note exists, update it
                        val request = UpdateNoteRequest(
                            content = note.content,
                            tags = note.tags
                        )
                        apiService.updateNote(note.id, request)
                    } else {
                        // Note doesn't exist, create it
                        val request = CreateNoteRequest(
                            content = note.content,
                            type = note.type,
                            boatId = note.boatId,
                            tripId = note.tripId,
                            tags = note.tags
                        )
                        val createResponse = apiService.createNote(request)
                        if (createResponse.isSuccessful && createResponse.body() != null) {
                            val apiNote = createResponse.body()!!.data
                            // Update local note with server ID
                            val syncedNote = note.copy(id = apiNote.id)
                            database.noteDao().insertNote(syncedNote)
                        }
                    }
                    database.noteDao().markAsSynced(note.id)
                } catch (e: Exception) {
                    // Continue with next note
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}