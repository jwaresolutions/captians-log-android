package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.TodoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_items WHERE todoListId = :todoListId ORDER BY createdAt ASC")
    fun getTodoItemsByListId(todoListId: String): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getTodoItemById(id: String): TodoItemEntity?

    @Query("SELECT * FROM todo_items WHERE synced = 0")
    suspend fun getUnsyncedTodoItems(): List<TodoItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(todoItem: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItems(todoItems: List<TodoItemEntity>)

    @Update
    suspend fun updateTodoItem(todoItem: TodoItemEntity)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItemEntity)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun deleteTodoItemById(id: String)

    @Query("UPDATE todo_items SET synced = 1 WHERE id = :id")
    suspend fun markTodoItemAsSynced(id: String)

    @Query("DELETE FROM todo_items WHERE todoListId = :todoListId")
    suspend fun deleteTodoItemsByListId(todoListId: String)

    @Query("DELETE FROM todo_items")
    suspend fun deleteAllTodoItems()

    @Query("SELECT * FROM todo_items")
    suspend fun getAllTodoItemsSync(): List<TodoItemEntity>
}