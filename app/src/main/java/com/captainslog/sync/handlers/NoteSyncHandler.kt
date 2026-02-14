package com.captainslog.sync.handlers

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.network.models.CreateNoteRequest
import com.captainslog.repository.NoteRepository
import com.captainslog.sync.DataType
import com.captainslog.sync.HandlerSyncResult
import com.captainslog.sync.SyncHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteSyncHandler @Inject constructor(
    private val noteRepository: NoteRepository,
    private val connectionManager: ConnectionManager,
    private val database: AppDatabase
) : SyncHandler {

    companion object {
        private const val TAG = "NoteSyncHandler"
    }

    override val dataType = DataType.NOTES

    override suspend fun syncFromServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing notes from server...")
            val result = noteRepository.syncNotesFromApi()
            if (result.isFailure) {
                Log.w(TAG, "Failed to sync notes from server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                Log.d(TAG, "Note sync from server completed")
                HandlerSyncResult(success = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing notes from server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncToServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing notes to server...")
            val result = noteRepository.syncNotesToApi()
            if (result.isFailure) {
                Log.w(TAG, "Failed to sync notes to server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                Log.d(TAG, "Note sync to server completed")
                HandlerSyncResult(success = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing notes to server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncEntity(entityId: String): HandlerSyncResult {
        return try {
            val note = database.noteDao().getNoteById(entityId) ?: return HandlerSyncResult(
                success = false, errors = listOf("Note not found: $entityId")
            )
            val apiService = connectionManager.getApiService()

            val request = CreateNoteRequest(
                type = note.type,
                content = note.content,
                tags = note.tags,
                tripId = note.tripId,
                boatId = note.boatId
            )

            val response = apiService.createNote(request)
            if (response.isSuccessful) {
                database.noteDao().markAsSynced(entityId)
                Log.d(TAG, "Note synced successfully: $entityId")
                HandlerSyncResult(success = true, syncedCount = 1)
            } else {
                Log.e(TAG, "Failed to sync note: ${response.code()}")
                HandlerSyncResult(success = false, errors = listOf("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing note entity: $entityId", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }
}
