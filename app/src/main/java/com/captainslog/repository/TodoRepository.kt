package com.captainslog.repository

import android.util.Log
import com.captainslog.database.dao.TodoItemDao
import com.captainslog.database.dao.TodoListDao
import com.captainslog.database.entities.TodoItemEntity
import com.captainslog.database.entities.TodoListEntity
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
class TodoRepository constructor(
    private val connectionManager: ConnectionManager,
    private val todoListDao: TodoListDao,
    private val todoItemDao: TodoItemDao
) {
    companion object {
        private const val TAG = "TodoRepository"
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    // Todo Lists
    fun getAllTodoLists(): Flow<List<TodoListEntity>> = todoListDao.getAllTodoLists()

    fun getTodoListsByBoat(boatId: String): Flow<List<TodoListEntity>> = 
        todoListDao.getTodoListsByBoat(boatId)

    fun getGeneralTodoLists(): Flow<List<TodoListEntity>> = todoListDao.getGeneralTodoLists()

    suspend fun getTodoListById(id: String): TodoListEntity? = todoListDao.getTodoListById(id)

    fun getTodoListByIdFlow(id: String): Flow<TodoListEntity?> = 
        todoListDao.getAllTodoLists().map { lists -> lists.find { it.id == id } }

    suspend fun createTodoList(title: String, boatId: String? = null): Result<TodoListEntity> {
        return try {
            val now = Date()
            val localTodoList = TodoListEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                boatId = boatId,
                createdAt = now,
                updatedAt = now,
                synced = false
            )

            // Save locally first
            todoListDao.insertTodoList(localTodoList)
            Log.d(TAG, "Created local todo list: ${localTodoList.id}")

            // Try to sync to server
            try {
                val apiService = connectionManager.getApiService()
                val request = CreateTodoListRequest(title = title, boatId = boatId)
                val response = apiService.createTodoList(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val serverTodoList = response.body()!!.data
                    val syncedTodoList = localTodoList.copy(
                        id = serverTodoList.id,
                        createdAt = parseDate(serverTodoList.createdAt),
                        updatedAt = parseDate(serverTodoList.updatedAt),
                        synced = true
                    )

                    // Delete local version and insert synced version
                    todoListDao.deleteTodoListById(localTodoList.id)
                    todoListDao.insertTodoList(syncedTodoList)

                    Log.d(TAG, "Synced todo list to server: ${syncedTodoList.id}")
                    Result.success(syncedTodoList)
                } else {
                    Log.w(TAG, "Failed to sync todo list to server: ${response.code()}")
                    Result.success(localTodoList)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync todo list to server", e)
                Result.success(localTodoList)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create todo list", e)
            Result.failure(e)
        }
    }

    suspend fun updateTodoList(id: String, title: String?, boatId: String?): Result<TodoListEntity> {
        return try {
            val existingTodoList = todoListDao.getTodoListById(id)
                ?: return Result.failure(Exception("Todo list not found"))

            val updatedTodoList = existingTodoList.copy(
                title = title ?: existingTodoList.title,
                boatId = boatId ?: existingTodoList.boatId,
                updatedAt = Date(),
                synced = false
            )

            todoListDao.updateTodoList(updatedTodoList)
            Log.d(TAG, "Updated local todo list: $id")

            // Try to sync to server
            try {
                val apiService = connectionManager.getApiService()
                val request = UpdateTodoListRequest(title = title, boatId = boatId)
                val response = apiService.updateTodoList(id, request)

                if (response.isSuccessful && response.body() != null) {
                    val serverTodoList = response.body()!!.data
                    val syncedTodoList = updatedTodoList.copy(
                        updatedAt = parseDate(serverTodoList.updatedAt),
                        synced = true
                    )
                    
                    todoListDao.updateTodoList(syncedTodoList)
                    Log.d(TAG, "Synced todo list update to server: $id")
                    Result.success(syncedTodoList)
                } else {
                    Log.w(TAG, "Failed to sync todo list update to server: ${response.code()}")
                    Result.success(updatedTodoList)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync todo list update to server", e)
                Result.success(updatedTodoList)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update todo list", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTodoList(id: String): Result<Unit> {
        return try {
            // Delete locally first
            todoListDao.deleteTodoListById(id)
            Log.d(TAG, "Deleted local todo list: $id")

            // Try to sync deletion to server
            try {
                val apiService = connectionManager.getApiService()
                val response = apiService.deleteTodoList(id)
                if (response.isSuccessful) {
                    Log.d(TAG, "Synced todo list deletion to server: $id")
                } else {
                    Log.w(TAG, "Failed to sync todo list deletion to server: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync todo list deletion to server", e)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete todo list", e)
            Result.failure(e)
        }
    }

    // Todo Items
    fun getTodoItemsByListId(listId: String): Flow<List<TodoItemEntity>> = 
        todoItemDao.getTodoItemsByListId(listId)

    suspend fun getTodoItemById(id: String): TodoItemEntity? = todoItemDao.getTodoItemById(id)

    suspend fun createTodoItem(listId: String, content: String): Result<TodoItemEntity> {
        return try {
            val now = Date()
            val localTodoItem = TodoItemEntity(
                id = UUID.randomUUID().toString(),
                todoListId = listId,
                content = content,
                completed = false,
                completedAt = null,
                createdAt = now,
                updatedAt = now,
                synced = false
            )

            // Save locally first
            todoItemDao.insertTodoItem(localTodoItem)
            Log.d(TAG, "Created local todo item: ${localTodoItem.id}")

            // Try to sync to server
            try {
                val apiService = connectionManager.getApiService()
                val request = CreateTodoItemRequest(content = content)
                val response = apiService.createTodoItem(listId, request)
                
                if (response.isSuccessful && response.body() != null) {
                    val serverTodoItem = response.body()!!.data
                    val syncedTodoItem = localTodoItem.copy(
                        id = serverTodoItem.id,
                        createdAt = parseDate(serverTodoItem.createdAt),
                        updatedAt = parseDate(serverTodoItem.updatedAt),
                        synced = true
                    )

                    // Delete local version and insert synced version
                    todoItemDao.deleteTodoItemById(localTodoItem.id)
                    todoItemDao.insertTodoItem(syncedTodoItem)
                    
                    Log.d(TAG, "Synced todo item to server: ${syncedTodoItem.id}")
                    Result.success(syncedTodoItem)
                } else {
                    Log.w(TAG, "Failed to sync todo item to server: ${response.code()}")
                    Result.success(localTodoItem)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync todo item to server", e)
                Result.success(localTodoItem)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create todo item", e)
            Result.failure(e)
        }
    }

    suspend fun updateTodoItem(id: String, content: String?, completed: Boolean?): Result<TodoItemEntity> {
        return try {
            val existingTodoItem = todoItemDao.getTodoItemById(id)
                ?: return Result.failure(Exception("Todo item not found"))

            val now = Date()
            val updatedTodoItem = existingTodoItem.copy(
                content = content ?: existingTodoItem.content,
                completed = completed ?: existingTodoItem.completed,
                completedAt = if (completed == true) now else if (completed == false) null else existingTodoItem.completedAt,
                updatedAt = now,
                synced = false
            )

            todoItemDao.updateTodoItem(updatedTodoItem)
            Log.d(TAG, "Updated local todo item: $id")

            // Try to sync to server
            try {
                val apiService = connectionManager.getApiService()
                val request = UpdateTodoItemRequest(content = content, completed = completed)
                val response = apiService.updateTodoItem(id, request)
                
                if (response.isSuccessful && response.body() != null) {
                    val serverTodoItem = response.body()!!.data
                    val syncedTodoItem = updatedTodoItem.copy(
                        completed = serverTodoItem.completed,
                        completedAt = serverTodoItem.completedAt?.let { parseDate(it) },
                        updatedAt = parseDate(serverTodoItem.updatedAt),
                        synced = true
                    )

                    todoItemDao.updateTodoItem(syncedTodoItem)
                    Log.d(TAG, "Synced todo item update to server: $id")
                    Result.success(syncedTodoItem)
                } else {
                    Log.w(TAG, "Failed to sync todo item update to server: ${response.code()}")
                    Result.success(updatedTodoItem)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync todo item update to server", e)
                Result.success(updatedTodoItem)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update todo item", e)
            Result.failure(e)
        }
    }

    suspend fun toggleTodoItemCompletion(id: String): Result<TodoItemEntity> {
        return try {
            val existingTodoItem = todoItemDao.getTodoItemById(id)
                ?: return Result.failure(Exception("Todo item not found"))

            val now = Date()
            val newCompleted = !existingTodoItem.completed
            val updatedTodoItem = existingTodoItem.copy(
                completed = newCompleted,
                completedAt = if (newCompleted) now else null,
                updatedAt = now,
                synced = false
            )

            todoItemDao.updateTodoItem(updatedTodoItem)
            Log.d(TAG, "Toggled local todo item completion: $id -> $newCompleted")

            // Try to sync to server
            try {
                val apiService = connectionManager.getApiService()
                val response = apiService.toggleTodoItemCompletion(id)

                if (response.isSuccessful && response.body() != null) {
                    val serverTodoItem = response.body()!!.data
                    val syncedTodoItem = updatedTodoItem.copy(
                        completed = serverTodoItem.completed,
                        completedAt = serverTodoItem.completedAt?.let { parseDate(it) },
                        updatedAt = parseDate(serverTodoItem.updatedAt),
                        synced = true
                    )
                    
                    todoItemDao.updateTodoItem(syncedTodoItem)
                    Log.d(TAG, "Synced todo item completion toggle to server: $id")
                    Result.success(syncedTodoItem)
                } else {
                    Log.w(TAG, "Failed to sync todo item completion toggle to server: ${response.code()}")
                    Result.success(updatedTodoItem)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync todo item completion toggle to server", e)
                Result.success(updatedTodoItem)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle todo item completion", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTodoItem(id: String): Result<Unit> {
        return try {
            // Delete locally first
            todoItemDao.deleteTodoItemById(id)
            Log.d(TAG, "Deleted local todo item: $id")

            // Try to sync deletion to server
            try {
                val apiService = connectionManager.getApiService()
                val response = apiService.deleteTodoItem(id)
                if (response.isSuccessful) {
                    Log.d(TAG, "Synced todo item deletion to server: $id")
                } else {
                    Log.w(TAG, "Failed to sync todo item deletion to server: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync todo item deletion to server", e)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete todo item", e)
            Result.failure(e)
        }
    }

    // Sync operations
    suspend fun syncTodoLists(): Result<Unit> {
        return try {
            Log.d(TAG, "Starting todo lists sync")
            
            // Fetch from server
            val apiService = connectionManager.getApiService()
            val response = apiService.getTodoLists()
            if (response.isSuccessful && response.body() != null) {
                val serverTodoLists = response.body()!!.data
                
                // Convert to entities and insert
                val entities = serverTodoLists.map { serverTodoList ->
                    TodoListEntity(
                        id = serverTodoList.id,
                        title = serverTodoList.title,
                        boatId = serverTodoList.boatId,
                        createdAt = parseDate(serverTodoList.createdAt),
                        updatedAt = parseDate(serverTodoList.updatedAt),
                        synced = true
                    )
                }
                
                todoListDao.insertTodoLists(entities)
                Log.d(TAG, "Synced ${entities.size} todo lists from server")
                
                // Sync items for each list
                for (serverTodoList in serverTodoLists) {
                    val itemEntities = serverTodoList.items.map { serverItem ->
                        TodoItemEntity(
                            id = serverItem.id,
                            todoListId = serverItem.todoListId,
                            content = serverItem.content,
                            completed = serverItem.completed,
                            completedAt = serverItem.completedAt?.let { parseDate(it) },
                            createdAt = parseDate(serverItem.createdAt),
                            updatedAt = parseDate(serverItem.updatedAt),
                            synced = true
                        )
                    }
                    todoItemDao.insertTodoItems(itemEntities)
                }
                
                Log.d(TAG, "Todo lists sync completed successfully")
                Result.success(Unit)
            } else {
                Log.w(TAG, "Failed to fetch todo lists from server: ${response.code()}")
                Result.failure(Exception("Failed to fetch todo lists: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync todo lists", e)
            Result.failure(e)
        }
    }

    private fun parseDate(dateString: String): Date {
        return try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse date: $dateString", e)
            Date()
        }
    }
}