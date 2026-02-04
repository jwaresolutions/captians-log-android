package com.captainslog.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captainslog.database.entities.NoteEntity
import com.captainslog.viewmodel.NoteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen displaying list of all notes with filtering and search options.
 * Allows creating new notes, filtering by type, and searching by content.
 */
@Composable
fun NotesListScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel = viewModel(),
    onNoteClick: (String) -> Unit = {},
    onCreateNote: () -> Unit = {}
) {
    val selectedNoteType by viewModel.selectedNoteType.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    var showSearchBar by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Get notes based on current filter
    val notes by when {
        searchQuery.isNotEmpty() -> viewModel.searchNotes(searchQuery).collectAsState(initial = emptyList())
        selectedNoteType == "all" -> viewModel.getAllNotes().collectAsState(initial = emptyList())
        else -> viewModel.getNotesByType(selectedNoteType).collectAsState(initial = emptyList())
    }

    // Show error or success messages
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearSuccessMessage()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNote
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showSearchBar = !showSearchBar }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search notes"
                    )
                }
            }

            // Search bar
            if (showSearchBar) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { 
                        searchText = it
                        if (it.isEmpty()) {
                            viewModel.searchNotes("")
                        }
                    },
                    label = { Text("Search notes...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.searchNotes(searchText) }
                            ) {
                                Text("Search")
                            }
                        }
                    }
                )
            }

            // Filter chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterOptions = listOf(
                    "all" to "All",
                    "general" to "General",
                    "boat" to "Boat",
                    "trip" to "Trip"
                )
                
                items(filterOptions.size) { index ->
                    val (type, label) = filterOptions[index]
                    FilterChip(
                        selected = selectedNoteType == type,
                        onClick = { viewModel.setSelectedNoteType(type) },
                        label = { Text(label) }
                    )
                }
            }

            // Notes list
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isLoading && notes.isEmpty() -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    notes.isEmpty() -> {
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No notes found for \"$searchQuery\""
                            } else {
                                "No notes yet. Create your first note!"
                            },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(notes) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { onNoteClick(note.id) },
                                    onDelete = { viewModel.deleteNote(note.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Note type chip
                    AssistChip(
                        onClick = { },
                        label = { Text(note.type.capitalize()) },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Note content
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Tags
                    if (note.tags.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(note.tags) { tag ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { 
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }
                    }
                    
                    // Date and sync status
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateFormat.format(note.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (!note.synced) {
                            AssistChip(
                                onClick = { },
                                label = { 
                                    Text(
                                        text = "Not synced",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                }
                
                // Delete button
                TextButton(
                    onClick = onDelete
                ) {
                    Text("Delete")
                }
            }
        }
    }
}