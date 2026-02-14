package com.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.database.entities.TodoItemEntity
import com.captainslog.database.entities.TodoListEntity
import com.captainslog.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _selectedListId = MutableStateFlow<String?>(null)
    val selectedListId: StateFlow<String?> = _selectedListId.asStateFlow()

    // Todo Lists
    val allTodoLists: Flow<List<TodoListEntity>> = todoRepository.getAllTodoLists()

    fun getTodoListsByBoat(boatId: String): Flow<List<TodoListEntity>> =
        todoRepository.getTodoListsByBoat(boatId)

    val generalTodoLists: Flow<List<TodoListEntity>> = todoRepository.getGeneralTodoLists()

    // Todo Items for selected list
    val selectedListItems: Flow<List<TodoItemEntity>> = selectedListId
        .filterNotNull()
        .flatMapLatest { listId ->
            todoRepository.getTodoItemsByListId(listId)
        }

    // Selected todo list details
    val selectedTodoList: Flow<TodoListEntity?> = selectedListId
        .flatMapLatest { listId ->
            if (listId != null) {
                todoRepository.getTodoListByIdFlow(listId)
            } else {
                flowOf(null)
            }
        }

    fun selectTodoList(listId: String?) {
        _selectedListId.value = listId
    }

    fun createTodoList(title: String, boatId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            todoRepository.createTodoList(title, boatId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to create todo list"
                    )
                }
        }
    }

    fun updateTodoList(id: String, title: String?, boatId: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            todoRepository.updateTodoList(id, title, boatId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to update todo list"
                    )
                }
        }
    }

    fun deleteTodoList(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            todoRepository.deleteTodoList(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    // Clear selection if deleted list was selected
                    if (_selectedListId.value == id) {
                        _selectedListId.value = null
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to delete todo list"
                    )
                }
        }
    }

    fun createTodoItem(listId: String, content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            todoRepository.createTodoItem(listId, content)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to create todo item"
                    )
                }
        }
    }

    fun updateTodoItem(id: String, content: String?, completed: Boolean?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            todoRepository.updateTodoItem(id, content, completed)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to update todo item"
                    )
                }
        }
    }

    fun toggleTodoItemCompletion(id: String) {
        viewModelScope.launch {
            todoRepository.toggleTodoItemCompletion(id)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to toggle todo item completion"
                    )
                }
        }
    }

    fun deleteTodoItem(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            todoRepository.deleteTodoItem(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to delete todo item"
                    )
                }
        }
    }

    fun syncTodoLists() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            todoRepository.syncTodoLists()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to sync todo lists"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class TodoUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
