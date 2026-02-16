package com.captainslog.repository

import android.util.Log
import com.captainslog.database.dao.TodoItemDao
import com.captainslog.database.dao.TodoListDao
import com.captainslog.database.entities.TodoItemEntity
import com.captainslog.database.entities.TodoListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class TodoRepository(
    private val todoListDao: TodoListDao,
    private val todoItemDao: TodoItemDao
) {
    companion object {
        private const val TAG = "TodoRepository"
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
            val todoList = TodoListEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                boatId = boatId,
                createdAt = now,
                updatedAt = now,
                synced = false
            )

            // Save locally
            todoListDao.insertTodoList(todoList)
            Log.d(TAG, "Created local todo list: ${todoList.id}")
            Result.success(todoList)
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
            Result.success(updatedTodoList)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update todo list", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTodoList(id: String): Result<Unit> {
        return try {
            // Delete locally
            todoListDao.deleteTodoListById(id)
            Log.d(TAG, "Deleted local todo list: $id")
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
            val todoItem = TodoItemEntity(
                id = UUID.randomUUID().toString(),
                todoListId = listId,
                content = content,
                completed = false,
                completedAt = null,
                createdAt = now,
                updatedAt = now,
                synced = false
            )

            // Save locally
            todoItemDao.insertTodoItem(todoItem)
            Log.d(TAG, "Created local todo item: ${todoItem.id}")
            Result.success(todoItem)
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
            Result.success(updatedTodoItem)
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
            Result.success(updatedTodoItem)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle todo item completion", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTodoItem(id: String): Result<Unit> {
        return try {
            // Delete locally
            todoItemDao.deleteTodoItemById(id)
            Log.d(TAG, "Deleted local todo item: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete todo item", e)
            Result.failure(e)
        }
    }
}
