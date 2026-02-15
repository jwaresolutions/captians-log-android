package com.captainslog.ui.sharing

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.captainslog.database.AppDatabase
import com.captainslog.ui.components.CameraPreview
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.BoatImporter
import com.captainslog.sharing.BoatShareGenerator
import com.captainslog.sharing.ImportResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Screen for scanning boat QR codes to import boats from other devices.
 *
 * @param onBack Callback when back button is pressed
 * @param onBoatImported Callback when a boat is successfully imported
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanBoatScreen(
    onBack: () -> Unit,
    onBoatImported: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    database: AppDatabase,
    preScannedData: com.captainslog.sharing.models.BoatShareData? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            preScannedData != null || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var scannedBoatName by remember { mutableStateOf(preScannedData?.data?.name ?: "") }
    var scannedData by remember { mutableStateOf(preScannedData) }
    var isProcessing by remember { mutableStateOf(false) }
    var scanError by remember { mutableStateOf<String?>(null) }
    var importResult by remember { mutableStateOf<ImportResult?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // If pre-scanned data provided, show confirmation immediately
    LaunchedEffect(preScannedData) {
        if (preScannedData != null) {
            showConfirmationDialog = true
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission && preScannedData == null) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Handle import confirmation
    fun handleImport() {
        scannedData?.let { data ->
            scope.launch {
                try {
                    isProcessing = true
                    val securePreferences = SecurePreferences(context)
                    val importer = BoatImporter(database.boatDao(), securePreferences)

                    val result = withContext(Dispatchers.IO) {
                        importer.importBoat(data)
                    }

                    importResult = result
                    showConfirmationDialog = false
                    isProcessing = false
                } catch (e: Exception) {
                    scanError = "Import failed: ${e.message}"
                    isProcessing = false
                    showConfirmationDialog = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Boat QR Code") },
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
            when {
                !hasCameraPermission -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Camera permission is required to scan QR codes",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }) {
                            Text("Grant Permission")
                        }
                    }
                }
                importResult != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        when (val result = importResult) {
                            is ImportResult.Created -> {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Boat imported successfully!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = scannedBoatName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = {
                                    onBoatImported(result.id)
                                    onBack()
                                }) {
                                    Text("Done")
                                }
                            }
                            is ImportResult.Updated -> {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Boat updated successfully!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = scannedBoatName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = {
                                    onBoatImported(result.id)
                                    onBack()
                                }) {
                                    Text("Done")
                                }
                            }
                            is ImportResult.Skipped -> {
                                Text(
                                    text = "Import skipped: ${result.reason}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = onBack) {
                                    Text("Go Back")
                                }
                            }
                            is ImportResult.Error -> {
                                Text(
                                    text = "Import failed: ${result.message}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = onBack) {
                                    Text("Go Back")
                                }
                            }
                            null -> {
                                // Should not happen, but handle it
                                Text("Unknown state")
                            }
                        }
                    }
                }
                scanError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = scanError ?: "Unknown error",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            scanError = null
                        }) {
                            Text("Try Again")
                        }
                    }
                }
                else -> {
                    // Camera preview
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onQrCodeScanned = { qrContent ->
                            if (!isProcessing && scannedData == null) {
                                scope.launch {
                                    try {
                                        val generator = BoatShareGenerator(SecurePreferences(context))
                                        val data = withContext(Dispatchers.Default) {
                                            generator.parseQrData(qrContent)
                                        }

                                        if (data != null && data.type == "boat") {
                                            scannedData = data
                                            scannedBoatName = data.data.name
                                            showConfirmationDialog = true
                                        } else {
                                            scanError = "Invalid boat QR code"
                                        }
                                    } catch (e: Exception) {
                                        scanError = "Failed to parse QR code: ${e.message}"
                                    }
                                }
                            }
                        }
                    )

                    // Scanning overlay
                    if (isProcessing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = {
                showConfirmationDialog = false
                scannedData = null
            },
            title = { Text("Import Boat?") },
            text = {
                Column {
                    Text("Do you want to import this boat?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = scannedBoatName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { handleImport() },
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Import")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        scannedData = null
                    },
                    enabled = !isProcessing
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
