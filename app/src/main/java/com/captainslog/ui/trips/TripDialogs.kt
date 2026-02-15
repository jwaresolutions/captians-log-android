package com.captainslog.ui.trips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.captainslog.database.entities.TripEntity
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripEditDialog(
    trip: TripEntity,
    boats: List<com.captainslog.database.entities.BoatEntity>,
    onDismiss: () -> Unit,
    onSave: (TripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBoatId by remember { mutableStateOf(trip.boatId) }
    var selectedWaterType by remember { mutableStateOf(trip.waterType) }
    var selectedRole by remember { mutableStateOf(trip.role) }
    var bodyOfWater by remember { mutableStateOf(trip.bodyOfWater ?: "") }
    var boundaryClassification by remember { mutableStateOf(trip.boundaryClassification ?: "") }
    var distanceOffshore by remember { mutableStateOf(trip.distanceOffshore?.toString() ?: "") }

    var expandedBoat by remember { mutableStateOf(false) }
    var expandedWaterType by remember { mutableStateOf(false) }
    var expandedRole by remember { mutableStateOf(false) }
    var expandedBoundary by remember { mutableStateOf(false) }
    var distanceOffshoreError by remember { mutableStateOf(false) }

    val waterTypes = listOf("inland", "coastal", "offshore")
    val roles = listOf("master", "mate", "operator", "deckhand", "engineer", "other")
    val boundaryClassifications = listOf(
        "" to "None",
        "great_lakes" to "Great Lakes",
        "shoreward" to "Shoreward of Boundary",
        "seaward" to "Seaward of Boundary"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Trip",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Boat Selection
                ExposedDropdownMenuBox(
                    expanded = expandedBoat,
                    onExpandedChange = { expandedBoat = !expandedBoat }
                ) {
                    OutlinedTextField(
                        value = boats.find { it.id == selectedBoatId }?.name ?: selectedBoatId,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Boat") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBoat) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedBoat,
                        onDismissRequest = { expandedBoat = false }
                    ) {
                        boats.forEach { boat ->
                            DropdownMenuItem(
                                text = { Text(boat.name) },
                                onClick = {
                                    selectedBoatId = boat.id
                                    expandedBoat = false
                                }
                            )
                        }
                    }
                }

                // Water Type Selection
                ExposedDropdownMenuBox(
                    expanded = expandedWaterType,
                    onExpandedChange = { expandedWaterType = !expandedWaterType }
                ) {
                    OutlinedTextField(
                        value = selectedWaterType.replaceFirstChar { it.uppercase() },
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Water Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWaterType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedWaterType,
                        onDismissRequest = { expandedWaterType = false }
                    ) {
                        waterTypes.forEach { waterType ->
                            DropdownMenuItem(
                                text = { Text(waterType.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedWaterType = waterType
                                    expandedWaterType = false
                                }
                            )
                        }
                    }
                }

                // Role Selection
                ExposedDropdownMenuBox(
                    expanded = expandedRole,
                    onExpandedChange = { expandedRole = !expandedRole }
                ) {
                    OutlinedTextField(
                        value = selectedRole.replaceFirstChar { it.uppercase() },
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedRole = role
                                    expandedRole = false
                                }
                            )
                        }
                    }
                }

                // Body of Water
                OutlinedTextField(
                    value = bodyOfWater,
                    onValueChange = { bodyOfWater = it },
                    label = { Text("Body of Water") },
                    placeholder = { Text("e.g., Chesapeake Bay") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Boundary Classification
                ExposedDropdownMenuBox(
                    expanded = expandedBoundary,
                    onExpandedChange = { expandedBoundary = !expandedBoundary }
                ) {
                    OutlinedTextField(
                        value = boundaryClassifications.find { it.first == boundaryClassification }?.second ?: "None",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Boundary Classification") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBoundary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedBoundary,
                        onDismissRequest = { expandedBoundary = false }
                    ) {
                        boundaryClassifications.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    boundaryClassification = value
                                    expandedBoundary = false
                                }
                            )
                        }
                    }
                }

                // Distance Offshore
                OutlinedTextField(
                    value = distanceOffshore,
                    onValueChange = {
                        distanceOffshore = it
                        distanceOffshoreError = false
                    },
                    label = { Text("Distance Offshore") },
                    placeholder = { Text("e.g., 12.5") },
                    suffix = { Text("nm") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = distanceOffshoreError,
                    supportingText = if (distanceOffshoreError) {
                        { Text("Please enter a valid number") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Validate distance offshore
                            var hasErrors = false
                            if (distanceOffshore.isNotEmpty()) {
                                try {
                                    val distance = distanceOffshore.toDouble()
                                    if (distance < 0) {
                                        distanceOffshoreError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    distanceOffshoreError = true
                                    hasErrors = true
                                }
                            }

                            if (!hasErrors) {
                                val updatedTrip = trip.copy(
                                    boatId = selectedBoatId,
                                    waterType = selectedWaterType,
                                    role = selectedRole,
                                    bodyOfWater = if (bodyOfWater.isNotEmpty()) bodyOfWater else null,
                                    boundaryClassification = if (boundaryClassification.isNotEmpty()) boundaryClassification else null,
                                    distanceOffshore = if (distanceOffshore.isNotEmpty()) distanceOffshore.toDouble() else null,
                                    synced = false, // Mark as not synced since we're updating
                                    lastModified = Date()
                                )
                                onSave(updatedTrip)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun ManualDataEditDialog(
    trip: TripEntity,
    onDismiss: () -> Unit,
    onSave: (TripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var engineHours by remember { mutableStateOf(trip.engineHours?.toString() ?: "") }
    var fuelConsumed by remember { mutableStateOf(trip.fuelConsumed?.toString() ?: "") }
    var weatherConditions by remember { mutableStateOf(trip.weatherConditions ?: "") }
    var numberOfPassengers by remember { mutableStateOf(trip.numberOfPassengers?.toString() ?: "") }
    var destination by remember { mutableStateOf(trip.destination ?: "") }
    var bodyOfWater by remember { mutableStateOf(trip.bodyOfWater ?: "") }
    var distanceOffshore by remember { mutableStateOf(trip.distanceOffshore?.toString() ?: "") }

    var engineHoursError by remember { mutableStateOf(false) }
    var fuelConsumedError by remember { mutableStateOf(false) }
    var numberOfPassengersError by remember { mutableStateOf(false) }
    var distanceOffshoreError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Manual Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Engine Hours
                OutlinedTextField(
                    value = engineHours,
                    onValueChange = {
                        engineHours = it
                        engineHoursError = false
                    },
                    label = { Text("Engine Hours") },
                    placeholder = { Text("e.g., 2.5") },
                    suffix = { Text("hrs") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = engineHoursError,
                    supportingText = if (engineHoursError) {
                        { Text("Please enter a valid number") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Fuel Consumed
                OutlinedTextField(
                    value = fuelConsumed,
                    onValueChange = {
                        fuelConsumed = it
                        fuelConsumedError = false
                    },
                    label = { Text("Fuel Consumed") },
                    placeholder = { Text("e.g., 15.2") },
                    suffix = { Text("gal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = fuelConsumedError,
                    supportingText = if (fuelConsumedError) {
                        { Text("Please enter a valid number") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Weather Conditions
                OutlinedTextField(
                    value = weatherConditions,
                    onValueChange = { weatherConditions = it },
                    label = { Text("Weather Conditions") },
                    placeholder = { Text("e.g., Sunny, light breeze") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Number of Passengers
                OutlinedTextField(
                    value = numberOfPassengers,
                    onValueChange = {
                        numberOfPassengers = it
                        numberOfPassengersError = false
                    },
                    label = { Text("Number of Passengers") },
                    placeholder = { Text("e.g., 3") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = numberOfPassengersError,
                    supportingText = if (numberOfPassengersError) {
                        { Text("Please enter a valid whole number") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Destination
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    placeholder = { Text("e.g., San Francisco Bay") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Body of Water
                OutlinedTextField(
                    value = bodyOfWater,
                    onValueChange = { bodyOfWater = it },
                    label = { Text("Body of Water") },
                    placeholder = { Text("e.g., Chesapeake Bay") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Distance Offshore
                OutlinedTextField(
                    value = distanceOffshore,
                    onValueChange = {
                        distanceOffshore = it
                        distanceOffshoreError = false
                    },
                    label = { Text("Distance Offshore") },
                    placeholder = { Text("e.g., 12.5") },
                    suffix = { Text("nm") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = distanceOffshoreError,
                    supportingText = if (distanceOffshoreError) {
                        { Text("Please enter a valid number") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Validate inputs
                            var hasErrors = false

                            if (engineHours.isNotEmpty()) {
                                try {
                                    engineHours.toDouble()
                                    if (engineHours.toDouble() < 0) {
                                        engineHoursError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    engineHoursError = true
                                    hasErrors = true
                                }
                            }

                            if (fuelConsumed.isNotEmpty()) {
                                try {
                                    fuelConsumed.toDouble()
                                    if (fuelConsumed.toDouble() < 0) {
                                        fuelConsumedError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    fuelConsumedError = true
                                    hasErrors = true
                                }
                            }

                            if (numberOfPassengers.isNotEmpty()) {
                                try {
                                    val passengers = numberOfPassengers.toInt()
                                    if (passengers < 0) {
                                        numberOfPassengersError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    numberOfPassengersError = true
                                    hasErrors = true
                                }
                            }

                            if (distanceOffshore.isNotEmpty()) {
                                try {
                                    val distance = distanceOffshore.toDouble()
                                    if (distance < 0) {
                                        distanceOffshoreError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    distanceOffshoreError = true
                                    hasErrors = true
                                }
                            }

                            if (!hasErrors) {
                                val updatedTrip = trip.copy(
                                    engineHours = if (engineHours.isNotEmpty()) engineHours.toDouble() else null,
                                    fuelConsumed = if (fuelConsumed.isNotEmpty()) fuelConsumed.toDouble() else null,
                                    weatherConditions = if (weatherConditions.isNotEmpty()) weatherConditions else null,
                                    numberOfPassengers = if (numberOfPassengers.isNotEmpty()) numberOfPassengers.toInt() else null,
                                    destination = if (destination.isNotEmpty()) destination else null,
                                    bodyOfWater = if (bodyOfWater.isNotEmpty()) bodyOfWater else null,
                                    distanceOffshore = if (distanceOffshore.isNotEmpty()) distanceOffshore.toDouble() else null,
                                    synced = false, // Mark as not synced since we're updating
                                    lastModified = Date()
                                )
                                onSave(updatedTrip)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
