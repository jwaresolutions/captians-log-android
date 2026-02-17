package com.captainslog.ui.trips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.captainslog.database.entities.BoatEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartTripDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String?, String?, Double?) -> Unit,
    boats: List<BoatEntity> = emptyList(),
    activeBoat: BoatEntity? = null
) {
    // Filter to only enabled boats
    val enabledBoats = boats.filter { it.enabled }

    // Initialize with active boat if available and enabled, otherwise first enabled boat, otherwise empty
    var selectedBoat by remember {
        mutableStateOf(
            if (activeBoat?.enabled == true) activeBoat else enabledBoats.firstOrNull()
        )
    }
    var waterType by remember { mutableStateOf("inland") }
    var role by remember { mutableStateOf("master") }
    var bodyOfWater by remember { mutableStateOf("") }
    var boundaryClassification by remember { mutableStateOf("") }
    var distanceOffshore by remember { mutableStateOf("") }
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start New Trip") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Show message if no boats exist
                if (boats.isEmpty()) {
                    Text(
                        text = "No boats available. Please create a boat first.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    // Boat dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedBoat,
                        onExpandedChange = { expandedBoat = it }
                    ) {
                        OutlinedTextField(
                            value = selectedBoat?.name ?: "Select a boat",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Boat") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBoat) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            isError = selectedBoat == null
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBoat,
                            onDismissRequest = { expandedBoat = false }
                        ) {
                            enabledBoats.forEach { boat ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(boat.name)
                                            if (boat.isActive) {
                                                Text(
                                                    text = "Active",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedBoat = boat
                                        expandedBoat = false
                                    }
                                )
                            }
                        }
                    }

                    // Water Type dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedWaterType,
                        onExpandedChange = { expandedWaterType = it }
                    ) {
                        OutlinedTextField(
                            value = waterType.replaceFirstChar { it.uppercase() },
                            onValueChange = {},
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
                            waterTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        waterType = type
                                        expandedWaterType = false
                                    }
                                )
                            }
                        }
                    }

                    // Role dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedRole,
                        onExpandedChange = { expandedRole = it }
                    ) {
                        OutlinedTextField(
                            value = role.replaceFirstChar { it.uppercase() },
                            onValueChange = {},
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
                            roles.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        role = r
                                        expandedRole = false
                                    }
                                )
                            }
                        }
                    }

                    // Body of Water text input
                    OutlinedTextField(
                        value = bodyOfWater,
                        onValueChange = { bodyOfWater = it },
                        label = { Text("Body of Water") },
                        placeholder = { Text("e.g., Chesapeake Bay") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Boundary Classification dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedBoundary,
                        onExpandedChange = { expandedBoundary = it }
                    ) {
                        OutlinedTextField(
                            value = boundaryClassifications.find { it.first == boundaryClassification }?.second ?: "None",
                            onValueChange = {},
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

                    // Distance Offshore numeric input
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
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedBoat?.let { boat ->
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
                            android.util.Log.d("StartTripDialog", "Starting trip for boat: ${boat.name} (${boat.id})")
                            onConfirm(
                                boat.id,
                                waterType,
                                role,
                                if (bodyOfWater.isNotEmpty()) bodyOfWater else null,
                                if (boundaryClassification.isNotEmpty()) boundaryClassification else null,
                                if (distanceOffshore.isNotEmpty()) distanceOffshore.toDouble() else null
                            )
                        }
                    }
                },
                enabled = selectedBoat != null && enabledBoats.isNotEmpty()
            ) {
                Text("Start")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
