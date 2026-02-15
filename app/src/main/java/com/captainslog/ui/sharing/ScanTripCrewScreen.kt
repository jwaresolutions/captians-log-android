package com.captainslog.ui.sharing

import android.graphics.Bitmap
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.captainslog.database.AppDatabase
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.ImportResult
import com.captainslog.sharing.TripCrewImporter
import com.captainslog.sharing.TripCrewShareGenerator
import com.captainslog.ui.components.CameraPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Screen for scanning trip crew QR codes to join trips as crew.
 *
 * @param onBack Callback when back button is pressed
 * @param onTripJoined Callback when successfully joined a trip
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanTripCrewScreen(
    onBack: () -> Unit,
    onTripJoined: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    database: AppDatabase,
    preScannedData: com.captainslog.sharing.models.TripCrewShareData? = null
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
    var showDisplayNameDialog by remember { mutableStateOf(false) }
    var displayNameInput by remember { mutableStateOf(SecurePreferences(context).username ?: "") }
    var scannedBoatName by remember { mutableStateOf(preScannedData?.data?.boatName ?: "") }
    var scannedCaptainName by remember { mutableStateOf(preScannedData?.data?.captainName ?: "") }
    var scannedWaterType by remember { mutableStateOf(preScannedData?.data?.waterType ?: "") }
    var scannedCrewCount by remember { mutableIntStateOf(preScannedData?.data?.crew?.size ?: 0) }
    var scannedData by remember { mutableStateOf(preScannedData) }
    var isProcessing by remember { mutableStateOf(false) }
    var scanError by remember { mutableStateOf<String?>(null) }
    var importResult by remember { mutableStateOf<ImportResult?>(null) }
    var crewResponseQrBitmap by remember { mutableStateOf<Bitmap?>(null) }

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

                    // Check if display name is set
                    if (securePreferences.displayName == null) {
                        isProcessing = false
                        showConfirmationDialog = false
                        showDisplayNameDialog = true
                        return@launch
                    }

                    val importer = TripCrewImporter(
                        tripDao = database.tripDao(),
                        boatDao = database.boatDao(),
                        crewMemberDao = database.crewMemberDao(),
                        securePreferences = securePreferences
                    )

                    val result = withContext(Dispatchers.IO) {
                        importer.importTrip(data)
                    }

                    importResult = result
                    showConfirmationDialog = false

                    // Generate crew response QR for captain to scan
                    if (result is ImportResult.Created || result is ImportResult.Updated) {
                        val generator = TripCrewShareGenerator(securePreferences)
                        val displayName = securePreferences.displayName ?: securePreferences.username ?: "Crew"
                        crewResponseQrBitmap = withContext(Dispatchers.Default) {
                            generator.generateCrewResponseQrBitmap(
                                deviceId = securePreferences.deviceId,
                                displayName = displayName,
                                tripId = data.data.tripId
                            )
                        }
                    }

                    isProcessing = false
                } catch (e: Exception) {
                    scanError = "Import failed: ${e.message}"
                    isProcessing = false
                    showConfirmationDialog = false
                }
            }
        }
    }

    // Handle display name submission and then import
    fun handleDisplayNameSubmit() {
        if (displayNameInput.isBlank()) {
            return
        }

        scope.launch {
            try {
                isProcessing = true
                val securePreferences = SecurePreferences(context)
                securePreferences.displayName = displayNameInput.trim()

                scannedData?.let { data ->
                    val importer = TripCrewImporter(
                        tripDao = database.tripDao(),
                        boatDao = database.boatDao(),
                        crewMemberDao = database.crewMemberDao(),
                        securePreferences = securePreferences
                    )

                    val result = withContext(Dispatchers.IO) {
                        importer.importTrip(data)
                    }

                    importResult = result

                    // Generate crew response QR for captain to scan
                    if (result is ImportResult.Created || result is ImportResult.Updated) {
                        val generator = TripCrewShareGenerator(securePreferences)
                        crewResponseQrBitmap = withContext(Dispatchers.Default) {
                            generator.generateCrewResponseQrBitmap(
                                deviceId = securePreferences.deviceId,
                                displayName = displayNameInput.trim(),
                                tripId = data.data.tripId
                            )
                        }
                    }

                    showDisplayNameDialog = false
                    isProcessing = false
                }
            } catch (e: Exception) {
                scanError = "Import failed: ${e.message}"
                isProcessing = false
                showDisplayNameDialog = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Trip") },
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
                            is ImportResult.Created, is ImportResult.Updated -> {
                                val resultId = when (result) {
                                    is ImportResult.Created -> result.id
                                    is ImportResult.Updated -> result.id
                                    else -> ""
                                }
                                val statusText = if (result is ImportResult.Created) "Joined trip successfully!" else "Trip updated successfully!"

                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = scannedBoatName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )

                                if (crewResponseQrBitmap != null) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        modifier = Modifier
                                            .size(280.dp)
                                            .padding(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                bitmap = crewResponseQrBitmap!!.asImageBitmap(),
                                                contentDescription = "Crew response QR code",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Show this to the captain",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = {
                                    onTripJoined(resultId)
                                    onBack()
                                }) {
                                    Text("Done")
                                }
                            }
                            is ImportResult.Skipped -> {
                                Text(
                                    text = "Join skipped: ${result.reason}",
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
                                    text = "Join failed: ${result.message}",
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
                                        val generator = TripCrewShareGenerator(SecurePreferences(context))
                                        val data = withContext(Dispatchers.Default) {
                                            generator.parseQrData(qrContent)
                                        }

                                        if (data != null && data.type == "crew_join") {
                                            scannedData = data
                                            scannedBoatName = data.data.boatName
                                            scannedCaptainName = data.data.captainName
                                            scannedWaterType = data.data.waterType
                                            scannedCrewCount = data.data.crew.size
                                            showConfirmationDialog = true
                                        } else {
                                            scanError = "Invalid trip crew QR code"
                                        }
                                    } catch (e: Exception) {
                                        scanError = "Failed to parse QR code: ${e.message}"
                                    }
                                }
                            }
                        }
                    )

                    // Scanning reticle overlay
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val reticleSize = size.minDimension * 0.6f
                        val left = (size.width - reticleSize) / 2
                        val top = (size.height - reticleSize) / 2
                        // Semi-transparent background outside reticle
                        drawRect(color = Color.Black.copy(alpha = 0.4f))
                        // Clear the reticle area
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.4f),
                            topLeft = Offset(left, top),
                            size = Size(reticleSize, reticleSize),
                            cornerRadius = CornerRadius(16f, 16f),
                            blendMode = androidx.compose.ui.graphics.BlendMode.Clear
                        )
                        // Draw reticle border
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(left, top),
                            size = Size(reticleSize, reticleSize),
                            cornerRadius = CornerRadius(16f, 16f),
                            style = Stroke(width = 4f)
                        )
                    }

                    // Instruction text
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Point camera at the trip QR code",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }

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
            title = { Text("Join as Crew?") },
            text = {
                Column {
                    Text("Do you want to join this trip as crew?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Boat: $scannedBoatName",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Captain: $scannedCaptainName",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Water Type: $scannedWaterType",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Crew Count: $scannedCrewCount",
                        style = MaterialTheme.typography.bodyMedium
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
                        Text("Join")
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

    // Display name dialog
    if (showDisplayNameDialog) {
        AlertDialog(
            onDismissRequest = {
                showDisplayNameDialog = false
                scannedData = null
                displayNameInput = ""
            },
            title = { Text("Enter Display Name") },
            text = {
                Column {
                    Text("Please enter your display name to join the trip:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = displayNameInput,
                        onValueChange = { displayNameInput = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { handleDisplayNameSubmit() },
                    enabled = !isProcessing && displayNameInput.isNotBlank()
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Continue")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDisplayNameDialog = false
                        scannedData = null
                        displayNameInput = ""
                    },
                    enabled = !isProcessing
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
