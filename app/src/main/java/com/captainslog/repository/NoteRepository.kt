package com.captainslog.repository

import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.NoteEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository for managing note data.
 * Handles local database operations only.
 */
class NoteRepository(
    private val database: AppDatabase
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
     * Create a new note locally
     */
    suspend fun createNote(
        content: String,
        type: String,
        boatId: String? = null,
        tripId: String? = null,
        tags: List<String> = emptyList()
    ): Result<NoteEntity> {
        return try {
            // Create note locally
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
            Result.success(note)
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
            val localTags = database.noteDao().getAllTags().flatMap { tagString ->
                if (tagString.isEmpty()) emptyList() else tagString.split(",")
            }.distinct()
            Result.success(localTags)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
