package com.captainslog.ui.notes

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.captainslog.ui.components.BreadcrumbItem

/**
 * Navigation component for the Notes section.
 * Handles navigation between notes list and note editor screens.
 */
@Composable
fun NotesNavigation(
    modifier: Modifier = Modifier,
    onBreadcrumbChanged: (List<BreadcrumbItem>, (() -> Unit)?) -> Unit = { _, _ -> }
) {
    var currentScreen by remember { mutableStateOf(NotesScreen.List) }
    var editingNoteId by remember { mutableStateOf<String?>(null) }
    var createNoteType by remember { mutableStateOf("general") }
    var createBoatId by remember { mutableStateOf<String?>(null) }
    var createTripId by remember { mutableStateOf<String?>(null) }

    // Report breadcrumbs on screen change
    LaunchedEffect(currentScreen) {
        val crumbs = when (currentScreen) {
            NotesScreen.List -> emptyList()
            NotesScreen.Editor -> listOf(BreadcrumbItem("Editor"))
        }
        val backToRoot: (() -> Unit)? = if (currentScreen != NotesScreen.List) {
            { currentScreen = NotesScreen.List }
        } else null
        onBreadcrumbChanged(crumbs, backToRoot)
    }

    when (currentScreen) {
        NotesScreen.List -> {
            NotesListScreen(
                modifier = modifier,
                onNoteClick = { noteId ->
                    editingNoteId = noteId
                    currentScreen = NotesScreen.Editor
                },
                onCreateNote = {
                    editingNoteId = null
                    createNoteType = "general"
                    createBoatId = null
                    createTripId = null
                    currentScreen = NotesScreen.Editor
                }
            )
        }
        
        NotesScreen.Editor -> {
            NoteEditorScreen(
                modifier = modifier,
                noteId = editingNoteId,
                initialNoteType = createNoteType,
                initialBoatId = createBoatId,
                initialTripId = createTripId,
                onNavigateBack = {
                    currentScreen = NotesScreen.List
                }
            )
        }
    }
}

/**
 * Enum representing the different screens in the Notes section
 */
enum class NotesScreen {
    List,
    Editor
}