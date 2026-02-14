package com.captainslog.ui.trips

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.viewmodel.TripTrackingViewModel
import com.captainslog.viewmodel.BoatViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.captainslog.ui.components.BreadcrumbItem

/**
 * Navigation component for trip-related screens.
 * Manages navigation between trip list, active trip, and trip detail screens.
 */
@Composable
fun TripNavigation(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    viewModel: TripTrackingViewModel = hiltViewModel(),
    boatViewModel: BoatViewModel = hiltViewModel(),
    onBreadcrumbChanged: (List<BreadcrumbItem>, (() -> Unit)?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Observe ViewModel state
    val isTracking by viewModel.isTracking.collectAsState()
    val currentTrip by viewModel.currentTrip.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val trips by viewModel.getAllTrips().collectAsState(initial = emptyList())
    
    // Observe boat data
    val boats by boatViewModel.getAllBoats().collectAsState(initial = emptyList())
    var activeBoat by remember { mutableStateOf<com.captainslog.database.entities.BoatEntity?>(null) }
    
    // Load active boat
    LaunchedEffect(boats) {
        activeBoat = boatViewModel.getActiveBoat()
    }
    
    // Navigation state
    var currentScreen by remember { mutableStateOf<TripScreen>(TripScreen.TripList) }
    var selectedTripId by remember { mutableStateOf<String?>(null) }
    var shareTripId by remember { mutableStateOf<String?>(null) }

    // Report breadcrumbs on screen change
    LaunchedEffect(currentScreen, shareTripId) {
        val crumbs = when {
            shareTripId != null -> listOf(BreadcrumbItem("Trips"), BreadcrumbItem("Share Trip"))
            currentScreen == TripScreen.TripDetail -> listOf(BreadcrumbItem("Trip Detail"))
            else -> emptyList()
        }
        val backToRoot: (() -> Unit)? = when {
            shareTripId != null -> { { shareTripId = null } }
            currentScreen != TripScreen.TripList -> { { currentScreen = TripScreen.TripList; selectedTripId = null } }
            else -> null
        }
        onBreadcrumbChanged(crumbs, backToRoot)
    }

    // Auto-navigate to TripDetail when tracking starts
    LaunchedEffect(isTracking, currentTrip) {
        val trip = currentTrip
        if (isTracking && trip != null && currentScreen == TripScreen.TripList) {
            selectedTripId = trip.id
            currentScreen = TripScreen.TripDetail
        }
    }
    
    // Bind to service on first composition
    DisposableEffect(Unit) {
        viewModel.bindToService(context)
        onDispose {
            viewModel.unbindFromService(context)
        }
    }
    
    when {
        shareTripId != null -> {
            val trip = trips.find { it.id == shareTripId }
            com.captainslog.ui.sharing.ShareTripScreen(
                tripId = shareTripId!!,
                tripName = trip?.let {
                    val boat = boats.find { b -> b.id == it.boatId }
                    "${boat?.name ?: "Unknown Boat"} - ${android.text.format.DateFormat.format("MMM dd, yyyy", it.startTime)}"
                },
                onBack = {
                    shareTripId = null
                }
            )
        }
        currentScreen == TripScreen.TripList -> {
            TripListScreen(
                trips = trips,
                onTripClick = { tripId ->
                    selectedTripId = tripId
                    currentScreen = TripScreen.TripDetail
                },
                onStartNewTrip = { boatId, waterType, role ->
                    viewModel.startTrip(
                        context = context,
                        boatId = boatId,
                        waterType = waterType,
                        role = role
                    )

                    // Navigate to trip detail after starting
                    scope.launch {
                        kotlinx.coroutines.delay(2000) // Wait for trip to start
                        viewModel.currentTripId.value?.let { tripId ->
                            selectedTripId = tripId
                            currentScreen = TripScreen.TripDetail
                        }
                    }
                },
                boats = boats,
                activeBoat = activeBoat,
                isTracking = isTracking,
                currentTrip = currentTrip,
                onForceCleanup = { viewModel.forceCleanup() },
                onRefreshState = { viewModel.refreshState() },
                onNuclearStop = { viewModel.forceStopEverything(context) },
                onShareTrip = { tripId ->
                    shareTripId = tripId
                },
                modifier = modifier
            )
        }
        currentScreen == TripScreen.TripDetail -> {
            selectedTripId?.let { tripId ->
                val trip = trips.find { it.id == tripId }
                val gpsPoints by viewModel.getGpsPointsForTrip(tripId)
                    .collectAsState(initial = emptyList())
                
                var statistics by remember { mutableStateOf<com.captainslog.repository.TripStatistics?>(null) }
                
                LaunchedEffect(tripId) {
                    scope.launch {
                        statistics = viewModel.calculateTripStatistics(tripId)
                    }
                }
                
                if (trip != null) {
                    TripDetailScreen(
                        trip = trip,
                        gpsPoints = gpsPoints,
                        statistics = statistics,
                        boats = boats,
                        onNavigateBack = {
                            currentScreen = TripScreen.TripList
                            selectedTripId = null
                        },
                        onStopTrip = {
                            viewModel.forceStopEverything(context)
                            currentScreen = TripScreen.TripList
                            selectedTripId = null
                        },
                        onUpdateManualData = { updatedTrip ->
                            scope.launch {
                                viewModel.updateTripManualData(updatedTrip)
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Sealed class representing different trip screens
 */
sealed class TripScreen {
    object TripList : TripScreen()
    object TripDetail : TripScreen()
}