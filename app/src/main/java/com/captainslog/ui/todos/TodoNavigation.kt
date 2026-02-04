package com.captainslog.ui.todos

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.captainslog.ui.components.BreadcrumbItem

@Composable
fun TodoNavigation(
    modifier: Modifier = Modifier,
    onBreadcrumbChanged: (List<BreadcrumbItem>, (() -> Unit)?) -> Unit = { _, _ -> }
) {
    var currentScreen by remember { mutableStateOf<TodoScreen>(TodoScreen.List) }
    var selectedListId by remember { mutableStateOf<String?>(null) }

    // Report breadcrumbs on screen change
    LaunchedEffect(currentScreen) {
        val crumbs = when (currentScreen) {
            TodoScreen.List -> emptyList()
            TodoScreen.Detail -> listOf(BreadcrumbItem("List Detail"))
        }
        val backToRoot: (() -> Unit)? = if (currentScreen != TodoScreen.List) {
            { currentScreen = TodoScreen.List; selectedListId = null }
        } else null
        onBreadcrumbChanged(crumbs, backToRoot)
    }

    when (currentScreen) {
        TodoScreen.List -> {
            TodoListScreen(
                modifier = modifier,
                onNavigateToTodoDetail = { listId ->
                    selectedListId = listId
                    currentScreen = TodoScreen.Detail
                }
            )
        }
        TodoScreen.Detail -> {
            selectedListId?.let { listId ->
                TodoDetailScreen(
                    modifier = modifier,
                    listId = listId,
                    onNavigateBack = {
                        currentScreen = TodoScreen.List
                        selectedListId = null
                    }
                )
            }
        }
    }
}

sealed class TodoScreen {
    object List : TodoScreen()
    object Detail : TodoScreen()
}
