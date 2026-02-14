package com.captainslog.ui.map

import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.repository.MarkedLocationWithDistance
import com.captainslog.viewmodel.MapViewModel
import com.captainslog.viewmodel.MapUiState
import com.captainslog.viewmodel.MapFilter
import com.captainslog.nautical.NauticalProviders
import com.captainslog.nautical.tile.NauticalTileSources
import com.captainslog.nautical.service.AISVessel
import com.captainslog.nautical.service.TideStation
import com.captainslog.nautical.service.MarineWeather
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Main map screen with OpenStreetMap integration.
 * Displays trip routes, marked locations, AIS vessels, tide stations,
 * marine weather, and nautical chart overlays with per-provider toggles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State
    val uiState by viewModel.uiState.collectAsState()
    var showLocationDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Debounce tracker for viewport changes
    var lastViewportCallTime by remember { mutableStateOf(0L) }

    // Cache tile providers so they persist across recompositions
    val nauticalTileProviders = remember {
        mutableMapOf<String, MapTileProviderBasic>()
    }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            getCurrentLocation(context) { location ->
                currentLocation = location
                mapView?.controller?.animateTo(
                    GeoPoint(location.latitude, location.longitude)
                )
            }
        }
    }

    // Get current location when permission is granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            getCurrentLocation(context) { location ->
                currentLocation = location
                viewModel.updateCurrentLocation(location.latitude, location.longitude)
            }
        }
    }

    // Load data
    LaunchedEffect(Unit) {
        viewModel.loadTrips()
        viewModel.loadMarkedLocations()
    }

    // Error display via Snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // OpenStreetMap with osmdroid
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                // Configure osmdroid
                Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))
                Configuration.getInstance().userAgentValue = "CaptainsLog"

                MapView(ctx).apply {
                    mapView = this

                    // Set tile source to OpenStreetMap
                    setTileSource(TileSourceFactory.MAPNIK)

                    // Enable built-in zoom controls
                    setBuiltInZoomControls(true)
                    setMultiTouchControls(true)

                    // Set initial position (Seattle, WA)
                    controller.setZoom(12.0)
                    controller.setCenter(GeoPoint(47.6062, -122.3321))

                    // Add location overlay if permission granted
                    if (hasLocationPermission) {
                        addLocationOverlay(this, ctx)
                    }

                    // Viewport change detection with debounce
                    addMapListener(object : MapListener {
                        override fun onScroll(event: ScrollEvent?): Boolean {
                            onViewportChanged(this@apply, viewModel, lastViewportCallTime) { newTime ->
                                lastViewportCallTime = newTime
                            }
                            return false
                        }

                        override fun onZoom(event: ZoomEvent?): Boolean {
                            onViewportChanged(this@apply, viewModel, lastViewportCallTime) { newTime ->
                                lastViewportCallTime = newTime
                            }
                            return false
                        }
                    })

                    // Initial nautical data load
                    val bb = boundingBox
                    viewModel.loadNauticalData(bb.latSouth, bb.lonWest, bb.latNorth, bb.lonEast)
                }
            },
            update = { mapViewInstance ->
                updateMapOverlays(mapViewInstance, uiState, viewModel, nauticalTileProviders)
            }
        )

        // Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Current location button
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        getCurrentLocation(context) { location ->
                            currentLocation = location
                            mapView?.controller?.animateTo(
                                GeoPoint(location.latitude, location.longitude)
                            )
                        }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "My Location"
                )
            }

            // Add location button
            FloatingActionButton(
                onClick = {
                    currentLocation?.let { location ->
                        selectedLocation = GeoPoint(location.latitude, location.longitude)
                        showLocationDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Location"
                )
            }
        }

        // Map controls overlay
        MapControlsOverlay(
            modifier = Modifier.align(Alignment.TopStart),
            uiState = uiState,
            enabledProviderIds = viewModel.getEnabledProviderIds(),
            isNoaaEnabled = viewModel.isProviderEnabled("noaa-charts"),
            isGebcoEnabled = viewModel.isProviderEnabled("gebco"),
            onToggleProvider = { id -> viewModel.toggleNauticalLayerVisibility(id) },
            onBaseMapModeChange = { mode -> viewModel.setBaseMapMode(mode) },
            onFilterChange = { filter ->
                viewModel.updateFilter(filter)
            },
            onRefresh = {
                viewModel.loadTrips()
                viewModel.loadMarkedLocations()
                mapView?.let { mv ->
                    val bb = mv.boundingBox
                    viewModel.loadNauticalData(bb.latSouth, bb.lonWest, bb.latNorth, bb.lonEast)
                }
            }
        )

        // Marine weather overlay (bottom-left)
        if (uiState.marineWeather != null && viewModel.isNauticalLayerVisible("open-meteo")) {
            MarineWeatherCard(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                weather = uiState.marineWeather!!
            )
        }

        // Loading indicator
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Snackbar host for error display
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Add location dialog
    if (showLocationDialog && selectedLocation != null) {
        AddLocationDialog(
            location = selectedLocation!!,
            onDismiss = {
                showLocationDialog = false
                selectedLocation = null
            },
            onConfirm = { name, category, notes, tags ->
                scope.launch {
                    viewModel.createMarkedLocation(
                        name = name,
                        latitude = selectedLocation!!.latitude,
                        longitude = selectedLocation!!.longitude,
                        category = category,
                        notes = notes,
                        tags = tags
                    )
                    showLocationDialog = false
                    selectedLocation = null
                }
            }
        )
    }
}

/**
 * Handle viewport changes with 2-second debounce.
 */
private fun onViewportChanged(
    mapView: MapView,
    viewModel: MapViewModel,
    lastCallTime: Long,
    updateTime: (Long) -> Unit
) {
    val now = System.currentTimeMillis()
    if (now - lastCallTime > 2000) {
        updateTime(now)
        val bb = mapView.boundingBox
        viewModel.loadNauticalData(bb.latSouth, bb.lonWest, bb.latNorth, bb.lonEast)

        // Save last map center for tile preloading on next launch
        val center = mapView.mapCenter
        mapView.context.getSharedPreferences("captains_log_prefs", android.content.Context.MODE_PRIVATE)
            .edit()
            .putFloat("last_map_lat", center.latitude.toFloat())
            .putFloat("last_map_lon", center.longitude.toFloat())
            .apply()
    }
}

/**
 * Get current location using FusedLocationProviderClient
 */
@Suppress("MissingPermission")
private fun getCurrentLocation(
    context: android.content.Context,
    onLocationReceived: (Location) -> Unit
) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let { onLocationReceived(it) }
        }
    } catch (e: Exception) {
        Log.e("MapScreen", "Error getting current location", e)
    }
}

/**
 * Add location overlay for showing current position
 */
@Suppress("MissingPermission")
private fun addLocationOverlay(mapView: MapView, context: android.content.Context) {
    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    locationOverlay.enableMyLocation()
    locationOverlay.enableFollowLocation()
    mapView.overlayManager.add(locationOverlay)
}

/**
 * Update all map overlays based on UI state.
 * This is the SOLE owner of overlay management -- no other function adds overlays.
 */
private fun updateMapOverlays(
    mapView: MapView,
    uiState: MapUiState,
    viewModel: MapViewModel,
    tileProviderCache: MutableMap<String, MapTileProviderBasic>
) {
    // 1. Clear ALL overlays except MyLocationNewOverlay and TilesOverlay
    val locationOverlay = mapView.overlayManager.filterIsInstance<MyLocationNewOverlay>()
    mapView.overlayManager.clear()
    locationOverlay.forEach { mapView.overlayManager.add(it) }

    // 2. Handle base map mode
    if (uiState.baseMapMode == "noaa-charts") {
        mapView.setTileSource(NauticalTileSources.noaaCharts)
    } else if (uiState.baseMapMode == "gebco") {
        mapView.setTileSource(NauticalTileSources.gebcoBathymetry)
    } else {
        // Standard OSM base map + nautical tile overlays
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        NauticalTileSources.tileProviderIds.forEach { id ->
            if (viewModel.isNauticalLayerVisible(id)) {
                val tileSource = NauticalTileSources.getSourceById(id) ?: return@forEach
                val provider = tileProviderCache.getOrPut(id) {
                    MapTileProviderBasic(mapView.context, tileSource)
                }
                val overlay = TilesOverlay(provider, mapView.context)
                overlay.setLoadingBackgroundColor(android.graphics.Color.TRANSPARENT)
                overlay.setLoadingLineColor(android.graphics.Color.TRANSPARENT)
                mapView.overlayManager.add(overlay)
            }
        }
    }

    // 3. Add trip routes
    if (uiState.filter.showTrips) {
        uiState.trips.forEach { trip ->
            val gpsPoints = uiState.tripGpsPoints[trip.id] ?: emptyList()
            if (gpsPoints.isNotEmpty()) {
                addTripRoute(mapView, trip, gpsPoints)
            }
        }
    }

    // 4. Add marked location markers
    if (uiState.filter.showMarkedLocations) {
        uiState.markedLocations.forEach { locationWithDistance ->
            addMarkedLocationMarker(mapView, locationWithDistance)
        }
    }

    // 5. Add AIS vessel markers (only if AIS layer is visible)
    if (viewModel.isNauticalLayerVisible("aisstream")) uiState.aisVessels.forEach { vessel ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(vessel.latitude, vessel.longitude)
            title = vessel.name.ifEmpty { "MMSI: ${vessel.mmsi}" }
            snippet = "Speed: ${"%.1f".format(vessel.speed)} kn | Heading: ${"%.0f".format(vessel.heading)}\u00B0"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_send)
            rotation = vessel.heading.toFloat()
        }
        mapView.overlayManager.add(marker)
    }

    // 6. Add tide station markers (only if NOAA layer is visible)
    if (viewModel.isNauticalLayerVisible("noaa-coops")) uiState.tideStations.forEach { station ->
        val predictions = uiState.tidePredictions[station.id]
        val latestPrediction = predictions?.firstOrNull()
        val tideInfo = if (latestPrediction != null) {
            "${"%.2f".format(latestPrediction.value)} ft (${latestPrediction.type})"
        } else null
        val marker = Marker(mapView).apply {
            position = GeoPoint(station.latitude, station.longitude)
            title = if (tideInfo != null) "${station.name} — $tideInfo" else station.name
            snippet = if (latestPrediction != null) {
                "@ ${latestPrediction.time}"
            } else {
                "Tide station"
            }
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_recent_history)
        }
        mapView.overlayManager.add(marker)
    }

    // 7. Invalidate to redraw
    mapView.invalidate()
}

/**
 * Add trip route as polyline overlay
 */
private fun addTripRoute(mapView: MapView, trip: TripEntity, gpsPoints: List<GpsPointEntity>) {
    val points = gpsPoints.map { point ->
        GeoPoint(point.latitude, point.longitude)
    }

    if (points.isEmpty()) return

    // Create polyline for route
    val polyline = Polyline().apply {
        setPoints(points)
        outlinePaint.color = Color.Blue.toArgb()
        outlinePaint.strokeWidth = 8f
        title = "Trip: ${trip.startTime}"
    }
    mapView.overlayManager.add(polyline)

    // Start marker
    val startMarker = Marker(mapView).apply {
        position = points.first()
        title = "Trip Start"
        snippet = "Started: ${trip.startTime}"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        icon = ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_mylocation)
    }
    mapView.overlayManager.add(startMarker)

    // End marker (if trip is completed)
    if (points.size > 1 && trip.endTime != null) {
        val endMarker = Marker(mapView).apply {
            position = points.last()
            title = "Trip End"
            snippet = "Ended: ${trip.endTime}"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_close_clear_cancel)
        }
        mapView.overlayManager.add(endMarker)
    }
}

/**
 * Add marked location as marker overlay
 */
private fun addMarkedLocationMarker(mapView: MapView, locationWithDistance: MarkedLocationWithDistance) {
    val location = locationWithDistance.location

    val marker = Marker(mapView).apply {
        position = GeoPoint(location.latitude, location.longitude)
        title = location.name
        snippet = buildString {
            append(location.category.replaceFirstChar { it.uppercase() })
            if (locationWithDistance.distanceMeters > 0) {
                append(" \u2022 ${String.format("%.1f", locationWithDistance.distanceMeters / 1000)} km")
            }
            location.notes?.let { notes ->
                if (notes.isNotEmpty()) {
                    append("\n$notes")
                }
            }
        }
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Set marker icon based on category
        icon = when (location.category) {
            "fishing" -> ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_compass)
            "marina" -> ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_directions)
            "anchorage" -> ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_mylocation)
            "hazard" -> ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_dialog_alert)
            else -> ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_mapmode)
        }
    }

    mapView.overlayManager.add(marker)
}

/**
 * Marine weather info card displayed in bottom-left corner.
 */
@Composable
private fun MarineWeatherCard(
    modifier: Modifier = Modifier,
    weather: MarineWeather
) {
    Card(
        modifier = modifier.width(180.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Marine Weather",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            weather.waveHeight?.let {
                Text(text = "Waves: ${"%.1f".format(it)} m", style = MaterialTheme.typography.bodySmall)
            }
            weather.windSpeed?.let {
                Text(text = "Wind: ${"%.0f".format(it)} km/h", style = MaterialTheme.typography.bodySmall)
            }
            weather.temperature?.let {
                Text(text = "Temp: ${"%.1f".format(it)}\u00B0C", style = MaterialTheme.typography.bodySmall)
            }
            weather.swellHeight?.let {
                Text(text = "Swell: ${"%.1f".format(it)} m", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/**
 * Map controls overlay with a toggle button and expandable panel
 * containing base map selector, overlay toggles, data layers, and refresh.
 */
@Composable
private fun MapControlsOverlay(
    modifier: Modifier = Modifier,
    uiState: MapUiState,
    enabledProviderIds: List<String>,
    isNoaaEnabled: Boolean,
    isGebcoEnabled: Boolean,
    onToggleProvider: (String) -> Unit,
    onBaseMapModeChange: (String) -> Unit,
    onFilterChange: (MapFilter) -> Unit,
    onRefresh: () -> Unit
) {
    var panelVisible by remember { mutableStateOf(false) }
    val isOsmBase = uiState.baseMapMode == "osm"

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Toggle button (always visible)
        SmallFloatingActionButton(
            onClick = { panelVisible = !panelVisible },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = "Map layers"
            )
        }

        // Expandable panel
        AnimatedVisibility(
            visible = panelVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Base Map section
                    Text(
                        text = "Base Map",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        FilterChip(
                            selected = isOsmBase,
                            onClick = { onBaseMapModeChange("osm") },
                            label = { Text("Standard", style = MaterialTheme.typography.labelSmall) }
                        )
                        if (isNoaaEnabled) {
                            FilterChip(
                                selected = uiState.baseMapMode == "noaa-charts",
                                onClick = { onBaseMapModeChange("noaa-charts") },
                                label = { Text("NOAA Charts", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                        if (isGebcoEnabled) {
                            FilterChip(
                                selected = uiState.baseMapMode == "gebco",
                                onClick = { onBaseMapModeChange("gebco") },
                                label = { Text("GEBCO Bathymetry", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    // Overlays section (only for standard OSM base map)
                    if (isOsmBase) {
                        HorizontalDivider()
                        Text(
                            text = "Overlays",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            enabledProviderIds.forEach { id ->
                                val providerName = NauticalProviders.getById(id)?.name ?: id
                                val isVisible = uiState.nauticalLayerVisibility[id] ?: true
                                FilterChip(
                                    selected = isVisible,
                                    onClick = { onToggleProvider(id) },
                                    label = { Text(providerName, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }

                    // Data Layers section
                    HorizontalDivider()
                    Text(
                        text = "Data Layers",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = uiState.filter.showTrips,
                            onClick = {
                                onFilterChange(uiState.filter.copy(showTrips = !uiState.filter.showTrips))
                            },
                            label = { Text("Trips") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                        FilterChip(
                            selected = uiState.filter.showMarkedLocations,
                            onClick = {
                                onFilterChange(uiState.filter.copy(showMarkedLocations = !uiState.filter.showMarkedLocations))
                            },
                            label = { Text("Locations") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }

                    // Refresh button
                    HorizontalDivider()
                    TextButton(
                        onClick = onRefresh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Refresh Data")
                    }
                }
            }
        }
    }
}
