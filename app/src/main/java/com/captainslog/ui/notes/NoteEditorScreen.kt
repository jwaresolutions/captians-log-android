package com.captainslog.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.viewmodel.BoatViewModel
import com.captainslog.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

/**
 * Screen for creating and editing notes.
 * Supports all note types (general, boat, trip) with tag management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    modifier: Modifier = Modifier,
    noteId: String? = null, // null for new note
    initialNoteType: String = "general",
    initialBoatId: String? = null,
    initialTripId: String? = null,
    onNavigateBack: () -> Unit,
    noteViewModel: NoteViewModel = viewModel(),
    boatViewModel: BoatViewModel = viewModel()
) {
    var content by remember { mutableStateOf("") }
    var noteType by remember { mutableStateOf(initialNoteType) }
    var selectedBoatId by remember { mutableStateOf(initialBoatId) }
    var selectedTripId by remember { mutableStateOf(initialTripId) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var newTag by remember { mutableStateOf("") }
    var showTagInput by remember { mutableStateOf(false) }
    
    val boats by boatViewModel.getAllBoats().collectAsState(initial = emptyList())
    val availableTags by noteViewModel.availableTags.collectAsState()
    val isLoading by noteViewModel.isLoading.collectAsState()
    val error by noteViewModel.error.collectAsState()
    val successMessage by noteViewModel.successMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Load existing note if editing
    LaunchedEffect(noteId) {
        noteId?.let { id ->
            noteViewModel.getNoteById(id)?.let { note ->
                content = note.content
                noteType = note.type
                selectedBoatId = note.boatId
                selectedTripId = note.tripId
                tags = note.tags
            }
        }
    }

    // Show error or success messages
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                noteViewModel.clearError()
            }
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                noteViewModel.clearSuccessMessage()
                if (it.contains("successfully")) {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (noteId == null) {
                        noteViewModel.createNote(
                            content = content,
                            type = noteType,
                            boatId = selectedBoatId,
                            tripId = selectedTripId,
                            tags = tags
                        )
                    } else {
                        noteViewModel.updateNote(
                            noteId = noteId,
                            content = content,
                            tags = tags
                        )
                    }
                },
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Note type selection (only for new notes)
            if (noteId == null) {
                Text(
                    text = "Note Type",
                    style = MaterialTheme.typography.titleMedium
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val noteTypes = listOf(
                        "general" to "General",
                        "boat" to "Boat",
                        "trip" to "Trip"
                    )
                    
                    items(noteTypes) { (type, label) ->
                        FilterChip(
                            selected = noteType == type,
                            onClick = { 
                                noteType = type
                                if (type != "boat") selectedBoatId = null
                                if (type != "trip") selectedTripId = null
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }

            // Boat selection (for boat-specific notes)
            if (noteType == "boat") {
                Text(
                    text = "Select Boat",
                    style = MaterialTheme.typography.titleMedium
                )
                
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = boats.find { it.id == selectedBoatId }?.name ?: "Select a boat",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
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

            // Content input
            Text(
                text = "Content",
                style = MaterialTheme.typography.titleMedium
            )
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Enter note content...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )

            // Tags section
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleMedium
            )
            
            // Current tags
            if (tags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tags) { tag ->
                        InputChip(
                            selected = false,
                            onClick = {
                                tags = tags.filter { it != tag }
                            },
                            label = { Text(tag) },
                            trailingIcon = {
                                Text("×", style = MaterialTheme.typography.titleMedium)
                            }
                        )
                    }
                }
            }
            
            // Add tag section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showTagInput) {
                    OutlinedTextField(
                        value = newTag,
                        onValueChange = { newTag = it },
                        label = { Text("New tag") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    TextButton(
                        onClick = {
                            if (newTag.isNotBlank() && !tags.contains(newTag)) {
                                tags = tags + newTag
                                newTag = ""
                                showTagInput = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                    
                    TextButton(
                        onClick = {
                            newTag = ""
                            showTagInput = false
                        }
                    ) {
                        Text("Cancel")
                    }
                } else {
                    Button(
                        onClick = { showTagInput = true }
                    ) {
                        Text("Add Tag")
                    }
                }
            }
            
            // Suggested tags from existing notes
            if (availableTags.isNotEmpty()) {
                Text(
                    text = "Suggested Tags",
                    style = MaterialTheme.typography.titleSmall
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableTags.filter { !tags.contains(it) }) { tag ->
                        SuggestionChip(
                            onClick = {
                                tags = tags + tag
                            },
                            label = { Text(tag) }
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}