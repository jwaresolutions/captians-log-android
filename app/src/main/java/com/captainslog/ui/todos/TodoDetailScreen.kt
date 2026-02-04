package com.captainslog.ui.todos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.TodoItemEntity
import com.captainslog.database.entities.TodoListEntity
import com.captainslog.viewmodel.BoatViewModel
import com.captainslog.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodoDetailScreen(
    modifier: Modifier = Modifier,
    listId: String,
    onNavigateBack: () -> Unit,
    todoViewModel: TodoViewModel = viewModel(),
    boatViewModel: BoatViewModel = viewModel()
) {
    val todoUiState by todoViewModel.uiState.collectAsStateWithLifecycle()
    val todoItems by todoViewModel.selectedListItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val todoList by todoViewModel.selectedTodoList.collectAsStateWithLifecycle(initialValue = null)
    val boats by boatViewModel.getAllBoats().collectAsState(initial = emptyList())
    
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showEditItemDialog by remember { mutableStateOf<TodoItemEntity?>(null) }
    var showDeleteItemDialog by remember { mutableStateOf<TodoItemEntity?>(null) }

    LaunchedEffect(listId) {
        todoViewModel.selectTodoList(listId)
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // List info
            todoList?.let { list ->
                val boat = boats.find { it.id == list.boatId }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (boat != null) {
                            Text(
                                text = "Boat: ${boat.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "General List",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        
                        val completedCount = todoItems.count { it.completed }
                        val totalCount = todoItems.size
                        Text(
                            text = "$completedCount of $totalCount items completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Loading indicator
            if (todoUiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Error message
            todoUiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Todo Items
            if (todoItems.isEmpty() && !todoUiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "No Items Yet",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add your first todo item using the + button above",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddItemDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Item")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todoItems) { item ->
                        TodoItemCard(
                            item = item,
                            onToggleComplete = { todoViewModel.toggleTodoItemCompletion(item.id) },
                            onEdit = { showEditItemDialog = item },
                            onDelete = { showDeleteItemDialog = item }
                        )
                    }
                }
            }
        }
    }

    // Add Item Dialog
    if (showAddItemDialog) {
        AddTodoItemDialog(
            onDismiss = { showAddItemDialog = false },
            onConfirm = { content ->
                todoViewModel.createTodoItem(listId, content)
                showAddItemDialog = false
            }
        )
    }

    // Edit Item Dialog
    showEditItemDialog?.let { item ->
        EditTodoItemDialog(
            item = item,
            onDismiss = { showEditItemDialog = null },
            onConfirm = { content ->
                todoViewModel.updateTodoItem(item.id, content, null)
                showEditItemDialog = null
            }
        )
    }

    // Delete Item Dialog
    showDeleteItemDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { showDeleteItemDialog = null },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        todoViewModel.deleteTodoItem(item.id)
                        showDeleteItemDialog = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteItemDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoItemCard(
    item: TodoItemEntity,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = item.completed,
                onCheckedChange = { onToggleComplete() }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.content,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (item.completed) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (item.completed) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Created: ${dateFormat.format(item.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (item.completed && item.completedAt != null) {
                    Text(
                        text = "Completed: ${dateFormat.format(item.completedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (!item.synced) {
                    Text(
                        text = "Not synced",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
private fun AddTodoItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(content) },
                enabled = content.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditTodoItemDialog(
    item: TodoItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var content by remember { mutableStateOf(item.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Item") },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(content) },
                enabled = content.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}