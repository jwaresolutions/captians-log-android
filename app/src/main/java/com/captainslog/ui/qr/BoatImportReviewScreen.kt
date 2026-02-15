package com.captainslog.ui.qr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.captainslog.database.AppDatabase
import com.captainslog.qr.QrBoatImporter
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Screen for reviewing and importing boat data from QR codes.
 *
 * Displays boat information preview, detects duplicates, and handles import operations.
 *
 * @param boatData JsonElement containing boat data from QR
 * @param qrId QR envelope ID for tracking
 * @param generatedAt ISO timestamp when QR was generated
 * @param onBack Callback when back button is pressed
 * @param onImportComplete Callback when import succeeds, passes boat ID
 * @param database Database instance
 * @param modifier Optional modifier for the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoatImportReviewScreen(
    boatData: JsonElement,
    qrId: String,
    generatedAt: String,
    onBack: () -> Unit,
    onImportComplete: (boatId: String) -> Unit,
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val importer = remember { QrBoatImporter(database.boatDao(), database.importedQrDao()) }

    // Parse boat data
    val boat = remember { importer.parseBoatData(boatData) }

    // State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var duplicateBoat by remember { mutableStateOf<com.captainslog.database.entities.BoatEntity?>(null) }
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var importedBoatName by remember { mutableStateOf("") }

    // Check for duplicates on load
    LaunchedEffect(boat.name, boat.officialNumber) {
        isLoading = true
        try {
            val duplicate = withContext(Dispatchers.IO) {
                importer.findDuplicate(boat.name, boat.officialNumber)
            }
            duplicateBoat = duplicate
        } catch (e: Exception) {
            errorMessage = "Error checking for duplicates: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    /**
     * Import boat as new entry
     */
    fun importAsNew() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val boatId = withContext(Dispatchers.IO) {
                    importer.importAsNew(boatData, qrId)
                }
                importedBoatName = boat.name
                showSuccessDialog = true
            } catch (e: Exception) {
                errorMessage = "Failed to import boat: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Update existing boat with QR data
     */
    fun updateExisting() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val boatId = withContext(Dispatchers.IO) {
                    importer.updateExisting(duplicateBoat!!.id, boatData, qrId)
                }
                importedBoatName = boat.name
                showSuccessDialog = true
            } catch (e: Exception) {
                errorMessage = "Failed to update boat: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Boat Import") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error message
                if (errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Duplicate warning
                if (duplicateBoat != null && !isLoading) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Duplicate Detected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "A boat named \"${duplicateBoat!!.name}\" already exists.",
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }

                // Vessel Information
                SectionCard(title = "Vessel Information") {
                    InfoRow(label = "Name", value = boat.name)
                    boat.officialNumber?.let { InfoRow(label = "Official Number", value = it) }
                    boat.grossTons?.let { InfoRow(label = "Gross Tons", value = it.toString()) }
                    boat.propulsionType?.let {
                        InfoRow(label = "Propulsion", value = formatPropulsionType(it))
                    }
                }

                // Dimensions
                if (boat.lengthFeet != null || boat.widthFeet != null || boat.depthFeet != null) {
                    SectionCard(title = "Dimensions") {
                        boat.lengthFeet?.let { feet ->
                            val inches = boat.lengthInches ?: 0
                            InfoRow(label = "Length", value = formatDimension(feet, inches))
                        }
                        boat.widthFeet?.let { feet ->
                            val inches = boat.widthInches ?: 0
                            InfoRow(label = "Width", value = formatDimension(feet, inches))
                        }
                        boat.depthFeet?.let { feet ->
                            val inches = boat.depthInches ?: 0
                            InfoRow(label = "Depth", value = formatDimension(feet, inches))
                        }
                    }
                }

                // Owner/Operator
                if (boat.ownerFirstName != null || boat.ownerLastName != null ||
                    boat.ownerStreetAddress != null || boat.ownerEmail != null ||
                    boat.ownerPhone != null) {
                    SectionCard(title = "Owner/Operator") {
                        val ownerName = listOfNotNull(
                            boat.ownerFirstName,
                            boat.ownerMiddleName,
                            boat.ownerLastName
                        ).joinToString(" ")

                        if (ownerName.isNotBlank()) {
                            InfoRow(label = "Name", value = ownerName)
                        }

                        boat.ownerStreetAddress?.let { InfoRow(label = "Street", value = it) }

                        val cityStateZip = listOfNotNull(
                            boat.ownerCity,
                            boat.ownerState,
                            boat.ownerZipCode
                        ).joinToString(", ")

                        if (cityStateZip.isNotBlank()) {
                            InfoRow(label = "Location", value = cityStateZip)
                        }

                        boat.ownerEmail?.let { InfoRow(label = "Email", value = it) }
                        boat.ownerPhone?.let { InfoRow(label = "Phone", value = it) }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Import button
                if (duplicateBoat != null) {
                    // Show choice buttons for duplicate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { updateExisting() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Update Existing")
                        }
                        OutlinedButton(
                            onClick = { importAsNew() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Create New")
                        }
                    }
                } else {
                    // Single import button
                    Button(
                        onClick = { importAsNew() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Import Boat")
                        }
                    }
                }
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Boat Imported Successfully!") },
            text = {
                Column {
                    Text("The boat \"$importedBoatName\" has been imported.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onImportComplete("")
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}

/**
 * Card for grouping related information
 */
@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * Display a label-value pair
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.width(120.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Format propulsion type for display
 */
private fun formatPropulsionType(type: String): String {
    return when (type.lowercase()) {
        "motor" -> "Motor"
        "steam" -> "Steam"
        "gas_turbine" -> "Gas Turbine"
        "sail" -> "Sail"
        "aux_sail" -> "Auxiliary Sail"
        else -> type.replaceFirstChar { it.uppercase() }
    }
}

/**
 * Format dimension as feet and inches
 */
private fun formatDimension(feet: Int, inches: Int): String {
    return if (inches > 0) {
        "$feet' $inches\""
    } else {
        "$feet'"
    }
}
