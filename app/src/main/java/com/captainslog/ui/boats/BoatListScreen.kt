package com.captainslog.ui.boats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.BoatEntity
import com.captainslog.viewmodel.BoatViewModel
import kotlinx.coroutines.launch

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
                    IconButton(onClick = onScanBoatQR) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "Scan boat QR code"
                        )
                    }
                    com.captainslog.ui.components.CompactSyncStatusIndicator(
                        onSyncClick = { viewModel.performFullSync() },
                        database = database
                    )
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
            onConfirm = { newName ->
                // TODO: Add updateBoat method to ViewModel
                // viewModel.updateBoat(editingBoat!!.id, newName)
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
                        
                        if (!boat.synced) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Not synced") }
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
                            onEdit = {
                                // TODO: Implement boat editing functionality
                                // For now, just show a placeholder message
                            }
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
 * Dialog for editing an existing boat.
 * Currently supports renaming boats.
 */
@Composable
fun EditBoatDialog(
    boat: BoatEntity,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var boatName by remember { mutableStateOf(boat.name) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Boat") },
        text = {
            Column {
                Text(
                    text = "Edit boat name:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = boatName,
                    onValueChange = { 
                        boatName = it
                        isError = it.isBlank()
                    },
                    label = { Text("Boat Name") },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Boat name cannot be empty") }
                    } else null,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (boatName.isNotBlank()) {
                        onConfirm(boatName.trim())
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}