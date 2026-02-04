package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE type = :type ORDER BY createdAt DESC")
    fun getNotesByType(type: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE boatId = :boatId ORDER BY createdAt DESC")
    fun getNotesByBoat(boatId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE tripId = :tripId ORDER BY createdAt DESC")
    fun getNotesByTrip(tripId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE tripId = :tripId ORDER BY createdAt DESC")
    suspend fun getNotesByTripSync(tripId: String): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE content LIKE '%' || :searchQuery || '%' ORDER BY createdAt DESC")
    fun searchNotes(searchQuery: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE synced = 0")
    suspend fun getUnsyncedNotes(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)

    @Query("UPDATE notes SET synced = 1 WHERE id = :noteId")
    suspend fun markAsSynced(noteId: String)

    @Query("SELECT DISTINCT tags FROM notes")
    suspend fun getAllTags(): List<String>

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    suspend fun getAllNotesSync(): List<NoteEntity>
}