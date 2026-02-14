package com.captainslog.ui.sharing

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.captainslog.database.entities.CrewMemberEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.TripCrewShareGenerator
import com.captainslog.ui.components.CameraPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for sharing a trip crew roster via QR code.
 * Captain shows this QR to crew members so they can join the trip.
 *
 * @param tripId The ID of the trip to share
 * @param onBack Callback when back button is pressed
 * @param onScanCrew Callback to navigate to scan screen for adding crew
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareTripCrewScreen(
    tripId: String,
    onBack: () -> Unit,
    onScanCrew: () -> Unit = {},
    modifier: Modifier = Modifier,
    database: AppDatabase
) {
    val context = LocalContext.current
    val securePreferences = remember { SecurePreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var trip by remember { mutableStateOf<TripEntity?>(null) }
    var boatName by remember { mutableStateOf<String?>(null) }
    val crewMembers by database.crewMemberDao().getCrewForTrip(tripId).collectAsState(initial = emptyList())
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var dataReady by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDisplayNameDialog by remember { mutableStateOf(false) }
    var displayNameInput by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    var isScanProcessing by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) isScanning = true
    }

    // Check if display name is set
    LaunchedEffect(Unit) {
        val displayName = securePreferences.displayName
        if (displayName == null) {
            showDisplayNameDialog = true
            displayNameInput = securePreferences.username ?: ""
        }
    }

    // Load trip and boat data
    LaunchedEffect(tripId) {
        try {
            isLoading = true
            error = null

            // Load trip from database
            val loadedTrip = withContext(Dispatchers.IO) {
                database.tripDao().getTripById(tripId)
            }

            if (loadedTrip == null) {
                error = "Trip not found"
                isLoading = false
                return@LaunchedEffect
            }

            trip = loadedTrip

            // Load boat name
            val boat = withContext(Dispatchers.IO) {
                database.boatDao().getBoatById(loadedTrip.boatId)
            }

            if (boat == null) {
                error = "Boat not found"
                isLoading = false
                return@LaunchedEffect
            }

            boatName = boat.name

            // Add captain to crew_members if not already present
            val deviceId = securePreferences.deviceId
            val captainName = securePreferences.displayName ?: securePreferences.username ?: "Captain"

            val existingCaptain = withContext(Dispatchers.IO) {
                database.crewMemberDao().getCrewForTripSync(tripId)
                    .find { it.deviceId == deviceId && it.role == "captain" }
            }

            if (existingCaptain == null) {
                withContext(Dispatchers.IO) {
                    database.crewMemberDao().insertCrewMember(
                        CrewMemberEntity(
                            tripId = tripId,
                            deviceId = deviceId,
                            displayName = captainName,
                            joinedAt = Date(),
                            role = "captain"
                        )
                    )
                }
            }

            isLoading = false
            dataReady = true
        } catch (e: Exception) {
            error = "Error loading trip: ${e.message}"
            isLoading = false
        }
    }

    // Regenerate QR code when crew list changes (only after data is loaded)
    // Use rememberCoroutineScope to avoid cancellation from LaunchedEffect key changes
    val qrScope = rememberCoroutineScope()
    var qrGenerationKey by remember { mutableStateOf(0) }

    // Track when inputs change to trigger QR regeneration
    LaunchedEffect(crewMembers.size, boatName, trip?.id, dataReady) {
        if (dataReady && trip != null && boatName != null) {
            qrGenerationKey++
        }
    }

    LaunchedEffect(qrGenerationKey) {
        if (qrGenerationKey == 0) return@LaunchedEffect
        val currentTrip = trip ?: return@LaunchedEffect
        val currentBoatName = boatName ?: return@LaunchedEffect
        val currentDisplayName = securePreferences.displayName ?: return@LaunchedEffect
        val currentCrew = crewMembers
        try {
            val generator = TripCrewShareGenerator(securePreferences)
            val bitmap = withContext(Dispatchers.Default) {
                generator.generateQrBitmap(
                    trip = currentTrip,
                    boatName = currentBoatName,
                    crewMembers = currentCrew,
                    size = 512
                )
            }
            qrBitmap = bitmap
        } catch (e: Exception) {
            error = "Error generating QR code: ${e.message}"
        }
    }

    // Display name dialog
    if (showDisplayNameDialog) {
        AlertDialog(
            onDismissRequest = { /* Don't allow dismissing without input */ },
            title = { Text("Set Your Display Name") },
            text = {
                Column {
                    Text(
                        "Please set a display name to appear on the crew roster.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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
                    onClick = {
                        if (displayNameInput.isNotBlank()) {
                            securePreferences.displayName = displayNameInput.trim()
                            showDisplayNameDialog = false
                        }
                    },
                    enabled = displayNameInput.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isScanning) "Scan Crew QR" else "Share Trip Crew") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isScanning) isScanning = false else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isScanning -> {
                    if (!hasCameraPermission) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Camera permission is required to scan crew QR codes", textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                                Text("Grant Permission")
                            }
                        }
                    } else {
                        CameraPreview(
                            modifier = Modifier.fillMaxSize(),
                            onQrCodeScanned = { qrContent ->
                                if (!isScanProcessing) {
                                    scope.launch {
                                        isScanProcessing = true
                                        try {
                                            val generator = TripCrewShareGenerator(securePreferences)
                                            val crewResponse = withContext(Dispatchers.Default) {
                                                generator.parseCrewResponse(qrContent)
                                            }
                                            if (crewResponse != null && crewResponse.tripId == tripId) {
                                                withContext(Dispatchers.IO) {
                                                    database.crewMemberDao().insertCrewMember(
                                                        CrewMemberEntity(
                                                            tripId = crewResponse.tripId,
                                                            deviceId = crewResponse.deviceId,
                                                            displayName = crewResponse.displayName,
                                                            joinedAt = Date(crewResponse.joinedAt),
                                                            role = "crew"
                                                        )
                                                    )
                                                }
                                                isScanning = false
                                                snackbarHostState.showSnackbar("${crewResponse.displayName} added to crew!")
                                            } else if (crewResponse != null) {
                                                isScanning = false
                                                snackbarHostState.showSnackbar("QR code is for a different trip")
                                            } else {
                                                isScanning = false
                                                snackbarHostState.showSnackbar("Invalid crew QR code")
                                            }
                                        } catch (e: Exception) {
                                            isScanning = false
                                            snackbarHostState.showSnackbar("Scan failed: ${e.message}")
                                        }
                                        isScanProcessing = false
                                    }
                                }
                            }
                        )

                        // Scanning reticle overlay
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val reticleSize = size.minDimension * 0.6f
                            val left = (size.width - reticleSize) / 2
                            val top = (size.height - reticleSize) / 2
                            drawRect(color = Color.Black.copy(alpha = 0.4f))
                            drawRoundRect(
                                color = Color.Black.copy(alpha = 0.4f),
                                topLeft = Offset(left, top),
                                size = Size(reticleSize, reticleSize),
                                cornerRadius = CornerRadius(16f, 16f),
                                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
                            )
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(left, top),
                                size = Size(reticleSize, reticleSize),
                                cornerRadius = CornerRadius(16f, 16f),
                                style = Stroke(width = 4f)
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Scan crew member's response QR code",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (isScanProcessing) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("Go Back")
                        }
                    }
                }
                trip != null && boatName != null && qrBitmap == null -> {
                    CircularProgressIndicator()
                }
                qrBitmap != null && trip != null && boatName != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Trip info header
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = boatName!!,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Started: ${SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(trip!!.startTime)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Water Type: ${trip!!.waterType.replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Captain: ${securePreferences.displayName ?: securePreferences.username}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // QR Code
                        Card(
                            modifier = Modifier
                                .size(320.dp)
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = qrBitmap!!.asImageBitmap(),
                                    contentDescription = "QR Code for trip crew",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Instructions
                        Text(
                            text = "Show this QR code to crew members to let them join this trip",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Scan Crew button
                        OutlinedButton(
                            onClick = {
                                if (hasCameraPermission) {
                                    isScanning = true
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Crew QR")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Crew list
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Current Crew (${crewMembers.size})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (crewMembers.isEmpty()) {
                                    Text(
                                        text = "No crew members yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    crewMembers.forEach { crew ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = crew.displayName,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    text = crew.role.replaceFirstChar { it.uppercase() },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(crew.joinedAt),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (crew != crewMembers.last()) {
                                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
