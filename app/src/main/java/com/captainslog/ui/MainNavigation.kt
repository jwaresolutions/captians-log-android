package com.captainslog.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.viewmodel.MainNavigationViewModel
import com.captainslog.ui.components.BreadcrumbItem
import com.captainslog.ui.home.HomeScreen
import com.captainslog.ui.license.LicenseProgressScreen
import com.captainslog.ui.map.MapScreen
import com.captainslog.ui.notes.NotesNavigation
import com.captainslog.ui.settings.SettingsScreen
import com.captainslog.ui.todos.TodoNavigation
import com.captainslog.ui.qr.BoatImportReviewScreen
import com.captainslog.ui.qr.QrImportScannerScreen
import com.captainslog.ui.qr.TripImportReviewScreen
import com.captainslog.ui.qr.UnifiedQrResult
import com.captainslog.ui.sharing.ScanBoatScreen
import com.captainslog.ui.sharing.ScanTripCrewScreen
import com.captainslog.ui.boats.BoatListScreen
import com.captainslog.ui.trips.TripNavigation
import com.captainslog.qr.QrTripImporter
import com.captainslog.sharing.models.BoatShareData
import com.captainslog.sharing.models.TripCrewShareData
import com.google.gson.JsonElement

/**
 * Main navigation structure with bottom navigation bar.
 * Top bar shows breadcrumb trail.
 */
@Composable
fun MainNavigation(
    viewModel: MainNavigationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(NavigationTab.Home) }

    // Track current screen (including top bar actions)
    var currentScreen: CurrentScreen by remember { mutableStateOf(CurrentScreen.Tab(NavigationTab.Home)) }

    // Nested breadcrumbs reported by child navigations
    var nestedBreadcrumbs by remember { mutableStateOf<List<BreadcrumbItem>>(emptyList()) }
    // Back handler from child navigation (navigate to child's root/list)
    var childBackHandler by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Bottom nav tabs (Home excluded - accessed via "Captain's Log" title)
    val availableTabs = NavigationTab.entries.filter { tab ->
        tab != NavigationTab.Home
    }

    // Build full breadcrumb list: top-level label + nested breadcrumbs
    val topLevelLabel = when (val screen = currentScreen) {
        is CurrentScreen.Tab -> when (screen.tab) {
            NavigationTab.Boats -> "Boats"
            NavigationTab.Home -> "Captain's Log"
            NavigationTab.Trips -> "Trips"
            NavigationTab.Map -> "Map"
            NavigationTab.License -> "License Progress"
        }
        CurrentScreen.Notes -> "Notes"
        CurrentScreen.Todos -> "Todos"
        CurrentScreen.Settings -> "Settings"
        CurrentScreen.QrImport -> "Import via QR"
    }

    val breadcrumbs = if (nestedBreadcrumbs.isEmpty()) {
        listOf(BreadcrumbItem(label = topLevelLabel))
    } else {
        val topClick = childBackHandler
        listOf(BreadcrumbItem(label = topLevelLabel, onClick = topClick)) + nestedBreadcrumbs
    }

    // Main navigation with integrated top bar actions
    Scaffold(
        topBar = {
            com.captainslog.ui.components.AppTopBar(
                breadcrumbs = breadcrumbs,
                onQrImportClick = {
                    nestedBreadcrumbs = emptyList()
                    currentScreen = CurrentScreen.QrImport
                },
                qrImportActive = currentScreen == CurrentScreen.QrImport,
                onNotesClick = {
                    nestedBreadcrumbs = emptyList()
                    currentScreen = CurrentScreen.Notes
                },
                onTodosClick = {
                    nestedBreadcrumbs = emptyList()
                    currentScreen = CurrentScreen.Todos
                },
                onSettingsClick = {
                    nestedBreadcrumbs = emptyList()
                    currentScreen = CurrentScreen.Settings
                },
                // Highlight active top bar button
                notesActive = currentScreen == CurrentScreen.Notes,
                todosActive = currentScreen == CurrentScreen.Todos,
                settingsActive = currentScreen == CurrentScreen.Settings,
                onTitleClick = {
                    nestedBreadcrumbs = emptyList()
                    childBackHandler = null
                    selectedTab = NavigationTab.Home
                    currentScreen = CurrentScreen.Tab(NavigationTab.Home)
                }
            )
        },
        bottomBar = {
            NavigationBar {
                availableTabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = currentScreen is CurrentScreen.Tab && (currentScreen as CurrentScreen.Tab).tab == tab,
                        onClick = {
                            selectedTab = tab
                            nestedBreadcrumbs = emptyList()
                            currentScreen = CurrentScreen.Tab(tab)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Handle Android back button
        BackHandler(enabled = nestedBreadcrumbs.isNotEmpty() || currentScreen !is CurrentScreen.Tab || currentScreen == CurrentScreen.QrImport) {
            if (nestedBreadcrumbs.isNotEmpty() && childBackHandler != null) {
                childBackHandler?.invoke()
            } else {
                nestedBreadcrumbs = emptyList()
                childBackHandler = null
                currentScreen = CurrentScreen.Tab(selectedTab)
            }
        }

        when (val screen = currentScreen) {
            is CurrentScreen.Tab -> {
                when (screen.tab) {
                    NavigationTab.Boats -> {
                        // Boats tab with sharing sub-screens
                        var boatShareBoatId by remember { mutableStateOf<String?>(null) }
                        var boatShowScanQR by remember { mutableStateOf(false) }

                        LaunchedEffect(boatShareBoatId, boatShowScanQR) {
                            val crumbs = when {
                                boatShareBoatId != null -> listOf(BreadcrumbItem("Share Boat"))
                                boatShowScanQR -> listOf(BreadcrumbItem("Scan Boat QR"))
                                else -> emptyList()
                            }
                            val back: (() -> Unit)? = when {
                                boatShareBoatId != null -> { { boatShareBoatId = null } }
                                boatShowScanQR -> { { boatShowScanQR = false } }
                                else -> null
                            }
                            nestedBreadcrumbs = crumbs
                            childBackHandler = back
                        }

                        if (boatShareBoatId != null) {
                            com.captainslog.ui.sharing.ShareBoatScreen(
                                boatId = boatShareBoatId!!,
                                onBack = { boatShareBoatId = null },
                                modifier = Modifier.padding(paddingValues),
                                database = viewModel.database
                            )
                        } else if (boatShowScanQR) {
                            ScanBoatScreen(
                                onBack = { boatShowScanQR = false },
                                onBoatImported = { boatShowScanQR = false },
                                modifier = Modifier.padding(paddingValues),
                                database = viewModel.database
                            )
                        } else {
                            BoatListScreen(
                                modifier = Modifier.padding(paddingValues),
                                database = viewModel.database,
                                onShareBoat = { boatId -> boatShareBoatId = boatId },
                                onScanBoatQR = { boatShowScanQR = true }
                            )
                        }
                    }
                    NavigationTab.Home -> {
                        HomeScreen(
                            modifier = Modifier.padding(paddingValues),
                            onNotesClick = {
                                nestedBreadcrumbs = emptyList()
                                currentScreen = CurrentScreen.Notes
                            },
                            onTodosClick = {
                                nestedBreadcrumbs = emptyList()
                                currentScreen = CurrentScreen.Todos
                            },
                            onSettingsClick = {
                                nestedBreadcrumbs = emptyList()
                                currentScreen = CurrentScreen.Settings
                            }
                        )
                    }
                    NavigationTab.Trips -> {
                        TripNavigation(
                            modifier = Modifier.padding(paddingValues),
                            database = viewModel.database,
                            onBreadcrumbChanged = { crumbs, backToRoot ->
                                nestedBreadcrumbs = crumbs
                                childBackHandler = backToRoot
                            }
                        )
                    }
                    NavigationTab.Map -> {
                        MapScreen(modifier = Modifier.padding(paddingValues))
                    }
                    NavigationTab.License -> {
                        LicenseProgressScreen(modifier = Modifier.padding(paddingValues))
                    }
                }
            }
            CurrentScreen.Notes -> {
                NotesNavigation(
                    modifier = Modifier.padding(paddingValues),
                    onBreadcrumbChanged = { crumbs, backToRoot ->
                        nestedBreadcrumbs = crumbs
                        childBackHandler = backToRoot
                    }
                )
            }
            CurrentScreen.Todos -> {
                TodoNavigation(
                    modifier = Modifier.padding(paddingValues),
                    onBreadcrumbChanged = { crumbs, backToRoot ->
                        nestedBreadcrumbs = crumbs
                        childBackHandler = backToRoot
                    }
                )
            }
            CurrentScreen.Settings -> {
                SettingsScreen(
                    modifier = Modifier.padding(paddingValues),
                    onNotesClick = {
                        nestedBreadcrumbs = emptyList()
                        currentScreen = CurrentScreen.Notes
                    },
                    onTodosClick = {
                        nestedBreadcrumbs = emptyList()
                        currentScreen = CurrentScreen.Todos
                    },
                    onBreadcrumbChanged = { crumbs, backToRoot ->
                        nestedBreadcrumbs = crumbs
                        childBackHandler = backToRoot
                    },
                    database = viewModel.database
                )
            }
            CurrentScreen.QrImport -> {
                // Internal QR import navigation
                var qrImportScreen by remember { mutableStateOf<QrImportScreen>(QrImportScreen.Scanner) }
                // Store decoded data for passing between screens
                var importData by remember { mutableStateOf<JsonElement?>(null) }
                var importQrId by remember { mutableStateOf<String?>(null) }
                var importGeneratedAt by remember { mutableStateOf<String?>(null) }
                var boatShareData by remember { mutableStateOf<BoatShareData?>(null) }
                var crewJoinData by remember { mutableStateOf<TripCrewShareData?>(null) }

                // Handle back within QR import flow
                BackHandler(enabled = qrImportScreen != QrImportScreen.Scanner) {
                    qrImportScreen = QrImportScreen.Scanner
                }

                when (qrImportScreen) {
                    QrImportScreen.Scanner -> {
                        QrImportScannerScreen(
                            onBack = { currentScreen = CurrentScreen.Tab(selectedTab) },
                            onScanResult = { result ->
                                when (result) {
                                    is UnifiedQrResult.WebImport -> {
                                        importData = result.data
                                        importQrId = result.qrId
                                        importGeneratedAt = result.generatedAt
                                        qrImportScreen = if (result.type == "trip") QrImportScreen.TripReview else QrImportScreen.BoatReview
                                    }
                                    is UnifiedQrResult.BoatShare -> {
                                        boatShareData = result.data
                                        qrImportScreen = QrImportScreen.DeviceBoatImport
                                    }
                                    is UnifiedQrResult.CrewJoin -> {
                                        crewJoinData = result.data
                                        qrImportScreen = QrImportScreen.CrewJoin
                                    }
                                    is UnifiedQrResult.CrewResponse -> {
                                        // Crew responses are typically handled during active sharing sessions.
                                        // For now, show the scanner again - this case shouldn't normally occur
                                        // from the unified scanner since captains use ShareTripCrewScreen.
                                        qrImportScreen = QrImportScreen.Scanner
                                    }
                                }
                            },
                            database = viewModel.database,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    QrImportScreen.TripReview -> {
                        val tripImporter = remember {
                            QrTripImporter(viewModel.database.tripDao(), viewModel.database.importedQrDao())
                        }
                        val parseResult = remember(importData) {
                            importData?.let { tripImporter.parseTripData(it) }
                        }
                        TripImportReviewScreen(
                            tripData = parseResult?.trips ?: emptyList(),
                            validationErrors = parseResult?.errors ?: emptyList(),
                            qrId = importQrId!!,
                            generatedAt = importGeneratedAt!!,
                            onBack = { qrImportScreen = QrImportScreen.Scanner },
                            onImportComplete = { currentScreen = CurrentScreen.Tab(selectedTab) },
                            database = viewModel.database,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    QrImportScreen.BoatReview -> {
                        BoatImportReviewScreen(
                            boatData = importData!!,
                            qrId = importQrId!!,
                            generatedAt = importGeneratedAt!!,
                            onBack = { qrImportScreen = QrImportScreen.Scanner },
                            onImportComplete = { currentScreen = CurrentScreen.Tab(selectedTab) },
                            database = viewModel.database,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    QrImportScreen.DeviceBoatImport -> {
                        ScanBoatScreen(
                            onBack = { qrImportScreen = QrImportScreen.Scanner },
                            onBoatImported = { currentScreen = CurrentScreen.Tab(selectedTab) },
                            database = viewModel.database,
                            modifier = Modifier.padding(paddingValues),
                            preScannedData = boatShareData
                        )
                    }
                    QrImportScreen.CrewJoin -> {
                        ScanTripCrewScreen(
                            onBack = { qrImportScreen = QrImportScreen.Scanner },
                            onTripJoined = { currentScreen = CurrentScreen.Tab(selectedTab) },
                            database = viewModel.database,
                            modifier = Modifier.padding(paddingValues),
                            preScannedData = crewJoinData
                        )
                    }
                }
            }
        }
    }
}

/**
 * Enum representing the main navigation tabs.
 */
enum class NavigationTab(val label: String, val icon: ImageVector) {
    Boats("Boats", Icons.Filled.DirectionsBoat),
    Home("Home", Icons.Filled.Home),
    Trips("Trips", Icons.Filled.List),
    Map("Map", Icons.Filled.LocationOn),
    License("License", Icons.Filled.Star)
}

/**
 * Sealed class representing all possible screens in the app.
 * Includes both bottom navigation tabs and top bar actions.
 */
sealed class CurrentScreen {
    data class Tab(val tab: NavigationTab) : CurrentScreen()
    object Notes : CurrentScreen()
    object Todos : CurrentScreen()
    object Settings : CurrentScreen()
    object QrImport : CurrentScreen()
}

/**
 * Internal navigation states for the QR import flow.
 */
private sealed class QrImportScreen {
    object Scanner : QrImportScreen()
    object TripReview : QrImportScreen()
    object BoatReview : QrImportScreen()
    object DeviceBoatImport : QrImportScreen()
    object CrewJoin : QrImportScreen()
}
