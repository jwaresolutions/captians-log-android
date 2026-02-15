package com.captainslog.ui.qr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.qr.QrTripImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Screen for reviewing and importing trips decoded from a QR code.
 *
 * Presents a list of trips to import, allows the user to select a boat,
 * assign roles to trips missing one, check for date conflicts, and
 * selectively skip conflicting trips before importing.
 *
 * @param tripData Parsed trip data from QR payload
 * @param qrId QR envelope ID for deduplication tracking
 * @param generatedAt ISO timestamp of when the QR was generated
 * @param onBack Navigate back
 * @param onImportComplete Called with the count of imported trips on success
 * @param database App database instance
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripImportReviewScreen(
    tripData: List<QrTripImporter.TripImportData>,
    qrId: String,
    generatedAt: String,
    onBack: () -> Unit,
    onImportComplete: (count: Int) -> Unit,
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val roles = listOf("master", "mate", "operator", "deckhand", "engineer", "other")

    // State
    var boats by remember { mutableStateOf<List<BoatEntity>>(emptyList()) }
    var selectedBoatId by remember { mutableStateOf<String?>(null) }
    var expandedBoatSelector by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isImporting by remember { mutableStateOf(false) }
    var isCheckingConflicts by remember { mutableStateOf(false) }
    var importedCount by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Per-trip role overrides (for trips where crewRole is null/blank)
    val roleOverrides = remember { mutableStateMapOf<Int, String>() }
    val expandedRoleSelectors = remember { mutableStateMapOf<Int, Boolean>() }

    // Conflict state
    var conflicts by remember { mutableStateOf<Map<Int, List<TripEntity>>>(emptyMap()) }
    var conflictsChecked by remember { mutableStateOf(false) }
    val skippedIndices = remember { mutableStateMapOf<Int, Boolean>() }

    val displayDateFormat = remember {
        SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
    }
    val gmtParseFormat = remember {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    // Load boats on mount
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            boats = database.boatDao().getAllBoatsSync()
        }
        if (boats.size == 1) {
            selectedBoatId = boats.first().id
        }
        isLoading = false
    }

    val importer = remember {
        QrTripImporter(database.tripDao(), database.importedQrDao())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Import") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Import complete state
            importedCount != null -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Import Complete",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$importedCount trip${if (importedCount != 1) "s" else ""} imported",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { onImportComplete(importedCount!!) }) {
                        Text("Done")
                    }
                }
            }

            // No boats available
            boats.isEmpty() -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No boats found. Please add a boat before importing trips.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = onBack) {
                        Text("Go Back")
                    }
                }
            }

            // Main review UI
            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    item {
                        Text(
                            text = "${tripData.size} trip${if (tripData.size != 1) "s" else ""} to import",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Boat selector
                    item {
                        Text(
                            text = "Select Boat",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedBoatSelector,
                            onExpandedChange = { expandedBoatSelector = !expandedBoatSelector }
                        ) {
                            OutlinedTextField(
                                value = boats.find { it.id == selectedBoatId }?.name ?: "Select a boat",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBoatSelector) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedBoatSelector,
                                onDismissRequest = { expandedBoatSelector = false }
                            ) {
                                boats.forEach { boat ->
                                    DropdownMenuItem(
                                        text = { Text(boat.name) },
                                        onClick = {
                                            selectedBoatId = boat.id
                                            expandedBoatSelector = false
                                            // Reset conflict state when boat changes
                                            conflicts = emptyMap()
                                            conflictsChecked = false
                                            skippedIndices.clear()
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Trip cards
                    itemsIndexed(tripData) { index, trip ->
                        val hasConflict = conflicts.containsKey(index)
                        val isSkipped = skippedIndices[index] == true
                        val needsRole = trip.crewRole.isNullOrBlank()

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isSkipped -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    hasConflict -> MaterialTheme.colorScheme.errorContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Trip header
                                Text(
                                    text = "Trip ${index + 1}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Date range
                                val startDisplay = try {
                                    gmtParseFormat.parse(trip.startDateGmt)?.let { displayDateFormat.format(it) }
                                } catch (_: Exception) { null } ?: trip.startDateGmt
                                val endDisplay = try {
                                    gmtParseFormat.parse(trip.endDateGmt)?.let { displayDateFormat.format(it) }
                                } catch (_: Exception) { null } ?: trip.endDateGmt

                                Text(
                                    text = "$startDisplay — $endDisplay",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Ports
                                Text(
                                    text = "${trip.departurePort} → ${trip.arrivalPort}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Water type & body of water
                                Text(
                                    text = "${trip.waterType.replaceFirstChar { it.uppercase() }} — ${trip.bodyOfWater}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Captain name if present
                                if (!trip.masterName.isNullOrBlank()) {
                                    Text(
                                        text = "Captain: ${trip.masterName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Role selector for trips missing crew role
                                if (needsRole) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val expanded = expandedRoleSelectors[index] == true
                                    val selectedRole = roleOverrides[index] ?: "master"
                                    Text(
                                        text = "Role",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = {
                                            expandedRoleSelectors[index] = !expanded
                                        }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedRole.replaceFirstChar { it.uppercase() },
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            textStyle = MaterialTheme.typography.bodyMedium
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expandedRoleSelectors[index] = false }
                                        ) {
                                            roles.forEach { role ->
                                                DropdownMenuItem(
                                                    text = { Text(role.replaceFirstChar { it.uppercase() }) },
                                                    onClick = {
                                                        roleOverrides[index] = role
                                                        expandedRoleSelectors[index] = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Conflict warning
                                if (hasConflict) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.Warning,
                                            contentDescription = "Conflict",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Conflicts with ${conflicts[index]!!.size} existing trip(s)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    // Show conflicting trip dates
                                    conflicts[index]!!.forEach { existing ->
                                        val existStart = displayDateFormat.format(existing.startTime)
                                        val existEnd = existing.endTime?.let { displayDateFormat.format(it) } ?: "ongoing"
                                        Text(
                                            text = "  Existing: $existStart — $existEnd",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isSkipped,
                                            onCheckedChange = { checked ->
                                                skippedIndices[index] = checked
                                            }
                                        )
                                        Text(
                                            text = "Skip this trip",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Error message
                    if (errorMessage != null) {
                        item {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Action buttons
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Check for Conflicts button
                            if (!conflictsChecked && selectedBoatId != null) {
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            isCheckingConflicts = true
                                            errorMessage = null
                                            try {
                                                val found = withContext(Dispatchers.IO) {
                                                    importer.findOverlaps(selectedBoatId!!, tripData)
                                                }
                                                conflicts = found
                                                conflictsChecked = true
                                                // Auto-skip conflicting trips
                                                found.keys.forEach { idx ->
                                                    skippedIndices[idx] = true
                                                }
                                            } catch (e: Exception) {
                                                errorMessage = "Error checking conflicts: ${e.message}"
                                            }
                                            isCheckingConflicts = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isCheckingConflicts
                                ) {
                                    if (isCheckingConflicts) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text("Check for Conflicts")
                                }
                            }

                            // Import button
                            val canImport = selectedBoatId != null && !isImporting
                            Button(
                                onClick = {
                                    scope.launch {
                                        isImporting = true
                                        errorMessage = null
                                        try {
                                            // Apply role overrides to trip data before import
                                            val adjustedTrips = tripData.mapIndexed { index, trip ->
                                                val overriddenRole = roleOverrides[index]
                                                if (overriddenRole != null && trip.crewRole.isNullOrBlank()) {
                                                    trip.copy(crewRole = overriddenRole)
                                                } else {
                                                    trip
                                                }
                                            }
                                            val skipSet = skippedIndices
                                                .filter { it.value }
                                                .keys
                                                .toSet()
                                            val count = withContext(Dispatchers.IO) {
                                                importer.importTrips(
                                                    boatId = selectedBoatId!!,
                                                    trips = adjustedTrips,
                                                    skipIndices = skipSet,
                                                    qrId = qrId
                                                )
                                            }
                                            importedCount = count
                                        } catch (e: Exception) {
                                            errorMessage = "Import failed: ${e.message}"
                                        }
                                        isImporting = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = canImport
                            ) {
                                if (isImporting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    if (conflictsChecked && skippedIndices.any { it.value })
                                        "Import ${tripData.size - skippedIndices.count { it.value }} Trip(s)"
                                    else
                                        "Import ${tripData.size} Trip(s)"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
