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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.TodoListEntity
import com.captainslog.viewmodel.BoatViewModel
import com.captainslog.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodoListScreen(
    modifier: Modifier = Modifier,
    onNavigateToTodoDetail: (String) -> Unit,
    todoViewModel: TodoViewModel = viewModel(),
    boatViewModel: BoatViewModel = viewModel()
) {
    val todoUiState by todoViewModel.uiState.collectAsStateWithLifecycle()
    val allTodoLists by todoViewModel.allTodoLists.collectAsStateWithLifecycle(initialValue = emptyList())
    val boats by boatViewModel.getAllBoats().collectAsState(initial = emptyList())
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<TodoListEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf<TodoListEntity?>(null) }

    LaunchedEffect(Unit) {
        todoViewModel.syncTodoLists()
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Todo List")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

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

            // Todo Lists
            if (allTodoLists.isEmpty() && !todoUiState.isLoading) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "No Todo Lists Yet",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create your first todo list to start organizing your tasks",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showCreateDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Todo List")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allTodoLists) { todoList ->
                        TodoListCard(
                            todoList = todoList,
                            boats = boats,
                            onNavigateToDetail = { onNavigateToTodoDetail(todoList.id) },
                            onEdit = { showEditDialog = todoList },
                            onDelete = { showDeleteDialog = todoList }
                        )
                    }
                }
            }
        }
    }
    
    // Create Dialog
    if (showCreateDialog) {
        CreateTodoListDialog(
            boats = boats,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, boatId ->
                todoViewModel.createTodoList(title, boatId)
                showCreateDialog = false
            }
        )
    }

    // Edit Dialog
    showEditDialog?.let { todoList ->
        EditTodoListDialog(
            todoList = todoList,
            boats = boats,
            onDismiss = { showEditDialog = null },
            onConfirm = { title, boatId ->
                todoViewModel.updateTodoList(todoList.id, title, boatId)
                showEditDialog = null
            }
        )
    }

    // Delete Dialog
    showDeleteDialog?.let { todoList ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Todo List") },
            text = { Text("Are you sure you want to delete \"${todoList.title}\"? This will also delete all items in the list.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        todoViewModel.deleteTodoList(todoList.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoListCard(
    todoList: TodoListEntity,
    boats: List<BoatEntity>,
    onNavigateToDetail: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val boat = boats.find { it.id == todoList.boatId }

    Card(
        onClick = onNavigateToDetail,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = todoList.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (boat != null) {
                        Text(
                            text = "Boat: ${boat.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "General List",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    
                    Text(
                        text = "Created: ${dateFormat.format(todoList.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (!todoList.synced) {
                        Text(
                            text = "Not synced",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                Row {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTodoListDialog(
    boats: List<BoatEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedBoatId by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Todo List") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = boats.find { it.id == selectedBoatId }?.name ?: "General List",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Boat") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("General List") },
                            onClick = {
                                selectedBoatId = null
                                expanded = false
                            }
                        )
                        boats.forEach { boat ->
                            DropdownMenuItem(
                                text = { Text(boat.name) },
                                onClick = {
                                    selectedBoatId = boat.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, selectedBoatId) },
                enabled = title.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTodoListDialog(
    todoList: TodoListEntity,
    boats: List<BoatEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var title by remember { mutableStateOf(todoList.title) }
    var selectedBoatId by remember { mutableStateOf(todoList.boatId) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Todo List") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = boats.find { it.id == selectedBoatId }?.name ?: "General List",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Boat") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("General List") },
                            onClick = {
                                selectedBoatId = null
                                expanded = false
                            }
                        )
                        boats.forEach { boat ->
                            DropdownMenuItem(
                                text = { Text(boat.name) },
                                onClick = {
                                    selectedBoatId = boat.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, selectedBoatId) },
                enabled = title.isNotBlank()
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