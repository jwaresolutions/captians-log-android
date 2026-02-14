package com.captainslog.sync.handlers

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.repository.TodoRepository
import com.captainslog.sync.DataType
import com.captainslog.sync.HandlerSyncResult
import com.captainslog.sync.SyncHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoSyncHandler @Inject constructor(
    private val todoRepository: TodoRepository,
    private val connectionManager: ConnectionManager
) : SyncHandler {

    companion object {
        private const val TAG = "TodoSyncHandler"
    }

    override val dataType = DataType.TODOS

    override suspend fun syncFromServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing todos from server...")
            val result = todoRepository.syncTodoLists()
            if (result.isFailure) {
                Log.w(TAG, "Failed to sync todos from server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                Log.d(TAG, "Todo sync from server completed")
                HandlerSyncResult(success = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing todos from server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncToServer(): HandlerSyncResult {
        // TODO: Add sync TO server for unsynced todos
        // Currently TodoRepository handles individual item sync but not bulk sync
        Log.d(TAG, "Todo sync to server - not yet implemented (individual items synced on change)")
        return HandlerSyncResult(success = true)
    }

    override suspend fun syncEntity(entityId: String): HandlerSyncResult {
        // TODO: Implement individual todo sync
        Log.d(TAG, "Todo entity sync not yet implemented: $entityId")
        return HandlerSyncResult(success = true)
    }
}
