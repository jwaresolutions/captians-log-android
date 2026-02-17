package com.captainslog.ui.boats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.BoatEntity
import com.captainslog.viewmodel.BoatViewModel
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Screen displaying list of all boats with management options.
 * Allows creating new boats, enabling/disabling boats, and setting active boat.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoatListScreen(
    modifier: Modifier = Modifier,
    viewModel: BoatViewModel = hiltViewModel(),
    database: AppDatabase,
    onShareBoat: (String) -> Unit = {},
    onScanBoatQR: () -> Unit = {}
) {
    val boats by viewModel.getAllBoats().collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    var showAddBoatDialog by remember { mutableStateOf(false) }
    var showEditBoatDialog by remember { mutableStateOf(false) }
    var editingBoat by remember { mutableStateOf<BoatEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show error or success messages
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearSuccessMessage()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Boats") },
                actions = {
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddBoatDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add boat")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && boats.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                boats.isEmpty() -> {
                    Text(
                        text = "No boats yet. Add your first boat!",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(boats) { boat ->
                            BoatCard(
                                boat = boat,
                                onToggleEnabled = { enabled ->
                                    viewModel.toggleBoatStatus(boat.id, enabled)
                                },
                                onSetActive = {
                                    viewModel.setActiveBoat(boat.id)
                                },
                                onEdit = {
                                    editingBoat = boat
                                    showEditBoatDialog = true
                                },
                                onShare = {
                                    onShareBoat(boat.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddBoatDialog) {
        AddBoatDialog(
            onDismiss = { showAddBoatDialog = false },
            onConfirm = { name ->
                viewModel.createBoat(name)
                showAddBoatDialog = false
            }
        )
    }

    if (showEditBoatDialog && editingBoat != null) {
        EditBoatDialog(
            boat = editingBoat!!,
            onDismiss = { 
                showEditBoatDialog = false
                editingBoat = null
            },
            onConfirm = { updatedBoat ->
                viewModel.updateBoatDetails(updatedBoat)
                showEditBoatDialog = false
                editingBoat = null
            }
        )
    }
}

@Composable
fun BoatCard(
    boat: BoatEntity,
    onToggleEnabled: (Boolean) -> Unit,
    onSetActive: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = boat.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        if (boat.isActive) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Active") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Enable/Disable toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (boat.enabled) "Enabled" else "Disabled",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = boat.enabled,
                        onCheckedChange = onToggleEnabled
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons - consistent 3-button layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit button (always visible)
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }

                // Status/Action button (middle)
                when {
                    boat.isActive -> {
                        // Show "Active" status button (disabled)
                        Button(
                            onClick = { /* No action - just shows status */ },
                            enabled = false,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Active")
                        }
                    }
                    boat.enabled -> {
                        // Show "Set Active" button
                        Button(
                            onClick = onSetActive,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Set Active")
                        }
                    }
                    else -> {
                        // Disabled boat - show disabled status
                        OutlinedButton(
                            onClick = { /* No action */ },
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Disabled")
                        }
                    }
                }

                // Share button
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }
        }
    }
}

/**
 * Content-only version of BoatListScreen for use within other Scaffolds.
 * This avoids nested Scaffold issues when called from SettingsScreen.
 */
@Composable
fun BoatListContent(
    modifier: Modifier = Modifier,
    viewModel: BoatViewModel = hiltViewModel()
) {
    val boats by viewModel.getAllBoats().collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show error or success messages
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearSuccessMessage()
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isLoading && boats.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            boats.isEmpty() -> {
                Text(
                    text = "No boats yet. Add your first boat!",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(boats) { boat ->
                        BoatCard(
                            boat = boat,
                            onToggleEnabled = { enabled ->
                                viewModel.toggleBoatStatus(boat.id, enabled)
                            },
                            onSetActive = {
                                viewModel.setActiveBoat(boat.id)
                            },
                            onEdit = { },
                            onShare = { }
                        )
                    }
                }
            }
        }

        // Snackbar host for messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Dialog for editing an existing boat with vessel details and owner information.
 * Uses Dialog with Card pattern for scrollable form.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBoatDialog(
    boat: BoatEntity,
    onDismiss: () -> Unit,
    onConfirm: (BoatEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Basic info
    var boatName by remember { mutableStateOf(boat.name) }
    var isNameError by remember { mutableStateOf(false) }

    // Vessel details
    var officialNumber by remember { mutableStateOf(boat.officialNumber ?: "") }
    var grossTons by remember { mutableStateOf(boat.grossTons?.toString() ?: "") }
    var lengthFeet by remember { mutableStateOf(boat.lengthFeet?.toString() ?: "") }
    var lengthInches by remember { mutableStateOf(boat.lengthInches?.toString() ?: "") }
    var widthFeet by remember { mutableStateOf(boat.widthFeet?.toString() ?: "") }
    var widthInches by remember { mutableStateOf(boat.widthInches?.toString() ?: "") }
    var depthFeet by remember { mutableStateOf(boat.depthFeet?.toString() ?: "") }
    var depthInches by remember { mutableStateOf(boat.depthInches?.toString() ?: "") }
    var selectedPropulsionType by remember { mutableStateOf(boat.propulsionType ?: "") }
    var expandedPropulsionType by remember { mutableStateOf(false) }

    // Owner/Operator info
    var ownerFirstName by remember { mutableStateOf(boat.ownerFirstName ?: "") }
    var ownerMiddleName by remember { mutableStateOf(boat.ownerMiddleName ?: "") }
    var ownerLastName by remember { mutableStateOf(boat.ownerLastName ?: "") }
    var ownerStreetAddress by remember { mutableStateOf(boat.ownerStreetAddress ?: "") }
    var ownerCity by remember { mutableStateOf(boat.ownerCity ?: "") }
    var ownerState by remember { mutableStateOf(boat.ownerState ?: "") }
    var ownerZipCode by remember { mutableStateOf(boat.ownerZipCode ?: "") }
    var ownerEmail by remember { mutableStateOf(boat.ownerEmail ?: "") }
    var ownerPhone by remember { mutableStateOf(boat.ownerPhone ?: "") }

    // Validation errors
    var grossTonsError by remember { mutableStateOf(false) }
    var lengthFeetError by remember { mutableStateOf(false) }
    var lengthInchesError by remember { mutableStateOf(false) }
    var widthFeetError by remember { mutableStateOf(false) }
    var widthInchesError by remember { mutableStateOf(false) }
    var depthFeetError by remember { mutableStateOf(false) }
    var depthInchesError by remember { mutableStateOf(false) }

    val propulsionTypes = listOf(
        "" to "Not specified",
        "motor" to "Motor",
        "steam" to "Steam",
        "gas_turbine" to "Gas Turbine",
        "sail" to "Sail",
        "aux_sail" to "Auxiliary Sail"
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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Boat",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Basic Info
                OutlinedTextField(
                    value = boatName,
                    onValueChange = {
                        boatName = it
                        isNameError = it.isBlank()
                    },
                    label = { Text("Boat Name") },
                    isError = isNameError,
                    supportingText = if (isNameError) {
                        { Text("Boat name cannot be empty") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Vessel Details Section
                Text(
                    text = "Vessel Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = officialNumber,
                    onValueChange = { officialNumber = it },
                    label = { Text("Official Number") },
                    placeholder = { Text("Optional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = grossTons,
                    onValueChange = {
                        grossTons = it
                        grossTonsError = false
                    },
                    label = { Text("Gross Tons") },
                    placeholder = { Text("Optional") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = grossTonsError,
                    supportingText = if (grossTonsError) {
                        { Text("Please enter a valid number") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Length (Feet + Inches side by side)
                Text(
                    text = "Length",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = lengthFeet,
                        onValueChange = {
                            lengthFeet = it
                            lengthFeetError = false
                        },
                        label = { Text("Feet") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = lengthFeetError,
                        supportingText = if (lengthFeetError) {
                            { Text("Invalid") }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lengthInches,
                        onValueChange = {
                            lengthInches = it
                            lengthInchesError = false
                        },
                        label = { Text("Inches") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = lengthInchesError,
                        supportingText = if (lengthInchesError) {
                            { Text("Invalid") }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Width (Feet + Inches side by side)
                Text(
                    text = "Width",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = widthFeet,
                        onValueChange = {
                            widthFeet = it
                            widthFeetError = false
                        },
                        label = { Text("Feet") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = widthFeetError,
                        supportingText = if (widthFeetError) {
                            { Text("Invalid") }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = widthInches,
                        onValueChange = {
                            widthInches = it
                            widthInchesError = false
                        },
                        label = { Text("Inches") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = widthInchesError,
                        supportingText = if (widthInchesError) {
                            { Text("Invalid") }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Depth (Feet + Inches side by side)
                Text(
                    text = "Depth",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = depthFeet,
                        onValueChange = {
                            depthFeet = it
                            depthFeetError = false
                        },
                        label = { Text("Feet") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = depthFeetError,
                        supportingText = if (depthFeetError) {
                            { Text("Invalid") }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = depthInches,
                        onValueChange = {
                            depthInches = it
                            depthInchesError = false
                        },
                        label = { Text("Inches") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = depthInchesError,
                        supportingText = if (depthInchesError) {
                            { Text("Invalid") }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Propulsion Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedPropulsionType,
                    onExpandedChange = { expandedPropulsionType = !expandedPropulsionType }
                ) {
                    OutlinedTextField(
                        value = propulsionTypes.find { it.first == selectedPropulsionType }?.second ?: "Not specified",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Propulsion Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPropulsionType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedPropulsionType,
                        onDismissRequest = { expandedPropulsionType = false }
                    ) {
                        propulsionTypes.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedPropulsionType = value
                                    expandedPropulsionType = false
                                }
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Owner/Operator Section
                Text(
                    text = "Owner/Operator",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = ownerFirstName,
                    onValueChange = { ownerFirstName = it },
                    label = { Text("First Name") },
                    placeholder = { Text("Optional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ownerMiddleName,
                    onValueChange = { ownerMiddleName = it },
                    label = { Text("Middle Name") },
                    placeholder = { Text("Optional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ownerLastName,
                    onValueChange = { ownerLastName = it },
                    label = { Text("Last Name") },
                    placeholder = { Text("Optional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ownerStreetAddress,
                    onValueChange = { ownerStreetAddress = it },
                    label = { Text("Street Address") },
                    placeholder = { Text("Optional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ownerCity,
                    onValueChange = { ownerCity = it },
                    label = { Text("City") },
                    placeholder = { Text("Optional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ownerState,
                        onValueChange = { ownerState = it },
                        label = { Text("State") },
                        placeholder = { Text("Optional") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = ownerZipCode,
                        onValueChange = { ownerZipCode = it },
                        label = { Text("Zip Code") },
                        placeholder = { Text("Optional") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = ownerEmail,
                    onValueChange = { ownerEmail = it },
                    label = { Text("Email") },
                    placeholder = { Text("Optional") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ownerPhone,
                    onValueChange = { ownerPhone = it },
                    label = { Text("Phone") },
                    placeholder = { Text("Optional") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
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
                            // Validate boat name
                            if (boatName.isBlank()) {
                                isNameError = true
                                return@Button
                            }

                            // Validate numeric fields
                            var hasErrors = false

                            if (grossTons.isNotEmpty()) {
                                try {
                                    val value = grossTons.toDouble()
                                    if (value < 0) {
                                        grossTonsError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    grossTonsError = true
                                    hasErrors = true
                                }
                            }

                            if (lengthFeet.isNotEmpty()) {
                                try {
                                    val value = lengthFeet.toInt()
                                    if (value < 0) {
                                        lengthFeetError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    lengthFeetError = true
                                    hasErrors = true
                                }
                            }

                            if (lengthInches.isNotEmpty()) {
                                try {
                                    val value = lengthInches.toInt()
                                    if (value < 0 || value >= 12) {
                                        lengthInchesError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    lengthInchesError = true
                                    hasErrors = true
                                }
                            }

                            if (widthFeet.isNotEmpty()) {
                                try {
                                    val value = widthFeet.toInt()
                                    if (value < 0) {
                                        widthFeetError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    widthFeetError = true
                                    hasErrors = true
                                }
                            }

                            if (widthInches.isNotEmpty()) {
                                try {
                                    val value = widthInches.toInt()
                                    if (value < 0 || value >= 12) {
                                        widthInchesError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    widthInchesError = true
                                    hasErrors = true
                                }
                            }

                            if (depthFeet.isNotEmpty()) {
                                try {
                                    val value = depthFeet.toInt()
                                    if (value < 0) {
                                        depthFeetError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    depthFeetError = true
                                    hasErrors = true
                                }
                            }

                            if (depthInches.isNotEmpty()) {
                                try {
                                    val value = depthInches.toInt()
                                    if (value < 0 || value >= 12) {
                                        depthInchesError = true
                                        hasErrors = true
                                    }
                                } catch (e: NumberFormatException) {
                                    depthInchesError = true
                                    hasErrors = true
                                }
                            }

                            if (!hasErrors) {
                                val updatedBoat = boat.copy(
                                    name = boatName.trim(),
                                    officialNumber = if (officialNumber.isNotEmpty()) officialNumber else null,
                                    grossTons = if (grossTons.isNotEmpty()) grossTons.toDouble() else null,
                                    lengthFeet = if (lengthFeet.isNotEmpty()) lengthFeet.toInt() else null,
                                    lengthInches = if (lengthInches.isNotEmpty()) lengthInches.toInt() else null,
                                    widthFeet = if (widthFeet.isNotEmpty()) widthFeet.toInt() else null,
                                    widthInches = if (widthInches.isNotEmpty()) widthInches.toInt() else null,
                                    depthFeet = if (depthFeet.isNotEmpty()) depthFeet.toInt() else null,
                                    depthInches = if (depthInches.isNotEmpty()) depthInches.toInt() else null,
                                    propulsionType = if (selectedPropulsionType.isNotEmpty()) selectedPropulsionType else null,
                                    ownerFirstName = if (ownerFirstName.isNotEmpty()) ownerFirstName else null,
                                    ownerMiddleName = if (ownerMiddleName.isNotEmpty()) ownerMiddleName else null,
                                    ownerLastName = if (ownerLastName.isNotEmpty()) ownerLastName else null,
                                    ownerStreetAddress = if (ownerStreetAddress.isNotEmpty()) ownerStreetAddress else null,
                                    ownerCity = if (ownerCity.isNotEmpty()) ownerCity else null,
                                    ownerState = if (ownerState.isNotEmpty()) ownerState else null,
                                    ownerZipCode = if (ownerZipCode.isNotEmpty()) ownerZipCode else null,
                                    ownerEmail = if (ownerEmail.isNotEmpty()) ownerEmail else null,
                                    ownerPhone = if (ownerPhone.isNotEmpty()) ownerPhone else null,
                                    lastModified = Date()
                                )
                                onConfirm(updatedBoat)
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