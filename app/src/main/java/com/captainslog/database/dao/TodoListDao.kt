package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.TodoListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoListDao {
    @Query("SELECT * FROM todo_lists ORDER BY createdAt DESC")
    fun getAllTodoLists(): Flow<List<TodoListEntity>>

    @Query("SELECT * FROM todo_lists WHERE boatId = :boatId ORDER BY createdAt DESC")
    fun getTodoListsByBoat(boatId: String): Flow<List<TodoListEntity>>

    @Query("SELECT * FROM todo_lists WHERE boatId IS NULL ORDER BY createdAt DESC")
    fun getGeneralTodoLists(): Flow<List<TodoListEntity>>

    @Query("SELECT * FROM todo_lists WHERE id = :id")
    suspend fun getTodoListById(id: String): TodoListEntity?

    @Query("SELECT * FROM todo_lists WHERE synced = 0")
    suspend fun getUnsyncedTodoLists(): List<TodoListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoList(todoList: TodoListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoLists(todoLists: List<TodoListEntity>)

    @Update
    suspend fun updateTodoList(todoList: TodoListEntity)

    @Delete
    suspend fun deleteTodoList(todoList: TodoListEntity)

    @Query("DELETE FROM todo_lists WHERE id = :id")
    suspend fun deleteTodoListById(id: String)

    @Query("UPDATE todo_lists SET synced = 1 WHERE id = :id")
    suspend fun markTodoListAsSynced(id: String)

    @Query("DELETE FROM todo_lists")
    suspend fun deleteAllTodoLists()

    @Query("SELECT * FROM todo_lists ORDER BY createdAt DESC")
    suspend fun getAllTodoListsSync(): List<TodoListEntity>
}