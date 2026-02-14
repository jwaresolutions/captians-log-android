package com.captainslog.ui.maintenance

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.ui.components.BreadcrumbItem
import com.captainslog.viewmodel.BoatViewModel
import com.captainslog.viewmodel.MaintenanceTemplateViewModel

@Composable
fun MaintenanceNavigation(
    modifier: Modifier = Modifier,
    onBreadcrumbChanged: (List<BreadcrumbItem>, (() -> Unit)?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val maintenanceViewModel: MaintenanceTemplateViewModel = hiltViewModel()
    val boatViewModel: BoatViewModel = hiltViewModel()

    var currentScreen by remember { mutableStateOf(MaintenanceScreen.List) }
    var selectedTemplateId by remember { mutableStateOf<String?>(null) }
    var selectedEventId by remember { mutableStateOf<String?>(null) }

    // Report breadcrumbs on screen change
    // Report breadcrumbs on screen change
    LaunchedEffect(currentScreen) {
        val crumbs = when (currentScreen) {
            MaintenanceScreen.List -> emptyList()
            MaintenanceScreen.TemplateDetail -> listOf(
                BreadcrumbItem("Template Details")
            )
            MaintenanceScreen.EventDetail -> listOf(
                BreadcrumbItem("Event Details")
            )
            MaintenanceScreen.CreateTemplate -> listOf(
                BreadcrumbItem("Create Template")
            )
            MaintenanceScreen.EditTemplate -> listOf(
                BreadcrumbItem("Template Details", onClick = { currentScreen = MaintenanceScreen.TemplateDetail }),
                BreadcrumbItem("Edit")
            )
            MaintenanceScreen.CompleteEvent -> listOf(
                BreadcrumbItem("Event Details", onClick = { currentScreen = MaintenanceScreen.EventDetail }),
                BreadcrumbItem("Complete")
            )
        }
        val backToRoot: (() -> Unit)? = if (currentScreen != MaintenanceScreen.List) {
            { currentScreen = MaintenanceScreen.List }
        } else null
        onBreadcrumbChanged(crumbs, backToRoot)
    }

    when (currentScreen) {
        MaintenanceScreen.List -> {
            MaintenanceListScreen(
                onNavigateToTemplateDetail = { templateId ->
                    selectedTemplateId = templateId
                    currentScreen = MaintenanceScreen.TemplateDetail
                },
                onNavigateToEventDetail = { eventId ->
                    selectedEventId = eventId
                    currentScreen = MaintenanceScreen.EventDetail
                },
                onNavigateToCreateTemplate = {
                    currentScreen = MaintenanceScreen.CreateTemplate
                },
                onNavigateToEditTemplate = { templateId ->
                    selectedTemplateId = templateId
                    currentScreen = MaintenanceScreen.EditTemplate
                },
                modifier = modifier,
                viewModel = maintenanceViewModel
            )
        }

        MaintenanceScreen.TemplateDetail -> {
            selectedTemplateId?.let { templateId ->
                MaintenanceTemplateDetailScreen(
                    templateId = templateId,
                    onNavigateBack = {
                        currentScreen = MaintenanceScreen.List
                        selectedTemplateId = null
                    },
                    onNavigateToEdit = { editTemplateId ->
                        selectedTemplateId = editTemplateId
                        currentScreen = MaintenanceScreen.EditTemplate
                    },
                    modifier = modifier,
                    viewModel = maintenanceViewModel
                )
            }
        }

        MaintenanceScreen.EventDetail -> {
            selectedEventId?.let { eventId ->
                MaintenanceEventDetailScreen(
                    eventId = eventId,
                    onNavigateBack = {
                        currentScreen = MaintenanceScreen.List
                        selectedEventId = null
                    },
                    onNavigateToTemplate = { templateId ->
                        selectedTemplateId = templateId
                        currentScreen = MaintenanceScreen.TemplateDetail
                    },
                    onNavigateToComplete = { completeEventId ->
                        selectedEventId = completeEventId
                        currentScreen = MaintenanceScreen.CompleteEvent
                    },
                    modifier = modifier,
                    viewModel = maintenanceViewModel
                )
            }
        }

        MaintenanceScreen.CreateTemplate -> {
            MaintenanceTemplateFormScreen(
                templateId = null,
                onNavigateBack = {
                    currentScreen = MaintenanceScreen.List
                },
                modifier = modifier,
                viewModel = maintenanceViewModel,
                boatViewModel = boatViewModel
            )
        }

        MaintenanceScreen.EditTemplate -> {
            selectedTemplateId?.let { templateId ->
                MaintenanceTemplateFormScreen(
                    templateId = templateId,
                    onNavigateBack = {
                        currentScreen = MaintenanceScreen.TemplateDetail
                    },
                    modifier = modifier,
                    viewModel = maintenanceViewModel,
                    boatViewModel = boatViewModel
                )
            }
        }

        MaintenanceScreen.CompleteEvent -> {
            selectedEventId?.let { eventId ->
                MaintenanceEventCompletionScreen(
                    eventId = eventId,
                    onNavigateBack = {
                        currentScreen = MaintenanceScreen.EventDetail
                    },
                    modifier = modifier,
                    viewModel = maintenanceViewModel
                )
            }
        }
    }
}

enum class MaintenanceScreen {
    List,
    TemplateDetail,
    EventDetail,
    CreateTemplate,
    EditTemplate,
    CompleteEvent
}
