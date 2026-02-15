package com.captainslog.ui.qr

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
import com.captainslog.database.entities.ImportedQrEntity
import com.captainslog.qr.QrAssembler
import com.captainslog.qr.QrProtocol
import com.captainslog.ui.components.CameraPreview
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Screen for scanning QR codes to import data into Captain's Log.
 *
 * Handles multi-QR sequential scanning, version validation, age warnings,
 * and duplicate detection for data imported from web forms.
 *
 * @param onBack Callback when back button is pressed
 * @param onImportReady Callback when QR data is successfully decoded and ready to import
 * @param database Database instance for duplicate checking
 * @param modifier Optional modifier for the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrImportScannerScreen(
    onBack: () -> Unit,
    onImportReady: (type: String, data: JsonElement, qrId: String, generatedAt: String) -> Unit,
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // QR assembly state
    val assembler = remember { QrAssembler() }
    var assemblyProgress by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var scanError by remember { mutableStateOf<String?>(null) }

    // Dialog state
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var showAgeWarningDialog by remember { mutableStateOf(false) }
    var pendingEnvelope by remember { mutableStateOf<QrProtocol.QrEnvelope?>(null) }
    var duplicateInfo by remember { mutableStateOf<ImportedQrEntity?>(null) }
    var generatedDate by remember { mutableStateOf<String?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * Continue processing an envelope (after user confirms warnings)
     */
    fun continueWithEnvelope(envelope: QrProtocol.QrEnvelope) {
        isProcessing = true
        scanError = null

        when (val result = assembler.addPart(envelope)) {
            is QrAssembler.AssemblyResult.NeedMore -> {
                assemblyProgress = Pair(result.collected, result.total)
                isProcessing = false
            }
            is QrAssembler.AssemblyResult.Complete -> {
                // Decode the complete data
                when (val decodeResult = QrProtocol.decodeComplete(
                    fullBase64Data = result.fullBase64Data,
                    type = result.type,
                    id = result.id,
                    generatedAt = result.generatedAt,
                    version = result.version
                )) {
                    is QrProtocol.QrDecodeResult.Success -> {
                        // Success - pass to callback
                        onImportReady(
                            decodeResult.type,
                            decodeResult.data,
                            decodeResult.id,
                            decodeResult.generatedAt
                        )
                        assembler.reset()
                        assemblyProgress = null
                        isProcessing = false
                    }
                    is QrProtocol.QrDecodeResult.InvalidFormat -> {
                        scanError = decodeResult.message
                        assembler.reset()
                        assemblyProgress = null
                        isProcessing = false
                    }
                    else -> {
                        scanError = "Failed to decode QR data"
                        assembler.reset()
                        assemblyProgress = null
                        isProcessing = false
                    }
                }
            }
            is QrAssembler.AssemblyResult.Error -> {
                scanError = result.message
                assembler.reset()
                assemblyProgress = null
                isProcessing = false
            }
        }
    }

    /**
     * Process a scanned QR envelope through the validation pipeline
     */
    suspend fun processEnvelope(envelope: QrProtocol.QrEnvelope) {
        isProcessing = true

        // Step 1: Validate version
        QrProtocol.validateVersion(envelope.version)?.let { versionError ->
            isProcessing = false
            scanError = when (versionError) {
                is QrProtocol.QrDecodeResult.VersionTooOld ->
                    "This QR code was created with an older format that is no longer supported. Please regenerate it at boat.jware.dev/tools/"
                is QrProtocol.QrDecodeResult.VersionTooNew ->
                    "This QR code requires a newer version of Captain's Log. Please update the app."
                else -> "Invalid QR version"
            }
            return
        }

        // Step 2: Check for duplicate (only on first part)
        if (envelope.part == 1) {
            val existingImport = withContext(Dispatchers.IO) {
                database.importedQrDao().getByQrId(envelope.id)
            }

            if (existingImport != null) {
                duplicateInfo = existingImport
                pendingEnvelope = envelope
                isProcessing = false
                showDuplicateDialog = true
                return
            }

            // Step 3: Check age (only on first part)
            if (QrProtocol.isExpired(envelope.generatedAt)) {
                try {
                    val instant = Instant.parse(envelope.generatedAt)
                    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                        .withZone(ZoneId.systemDefault())
                    generatedDate = formatter.format(instant)
                } catch (e: Exception) {
                    generatedDate = envelope.generatedAt
                }
                pendingEnvelope = envelope
                isProcessing = false
                showAgeWarningDialog = true
                return
            }
        }

        // Step 4: Feed to assembler
        continueWithEnvelope(envelope)
    }

    /**
     * Handle QR code scan from camera
     */
    fun handleQrScan(qrContent: String) {
        if (isProcessing) return

        scope.launch {
            try {
                // Parse envelope
                val envelope = QrProtocol.parseEnvelope(qrContent)
                if (envelope == null) {
                    scanError = "Invalid QR code"
                    return@launch
                }

                // Process through validation pipeline
                processEnvelope(envelope)
            } catch (e: Exception) {
                scanError = "Failed to process QR code: ${e.message}"
                isProcessing = false
            }
        }
    }

    /**
     * Reset scanner state and start over
     */
    fun resetScanner() {
        scanError = null
        assembler.reset()
        assemblyProgress = null
        isProcessing = false
        pendingEnvelope = null
        duplicateInfo = null
        generatedDate = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import via QR") },
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
                // Camera permission not granted
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

                // Error state
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
                        Button(onClick = { resetScanner() }) {
                            Text("Try Again")
                        }
                    }
                }

                // Camera preview with scanning overlay
                else -> {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onQrCodeScanned = { qrContent ->
                            handleQrScan(qrContent)
                        }
                    )

                    // Bottom overlay with status
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                when {
                                    isProcessing -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Processing...",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    assemblyProgress != null -> {
                                        val (collected, total) = assemblyProgress!!
                                        LinearProgressIndicator(
                                            progress = { collected.toFloat() / total.toFloat() },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Scanned $collected of $total — scan next QR code",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "Position QR code in camera view",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Duplicate QR warning dialog
    if (showDuplicateDialog && duplicateInfo != null) {
        val importedDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val importedDateStr = importedDateFormat.format(duplicateInfo!!.importedAt)

        AlertDialog(
            onDismissRequest = {
                showDuplicateDialog = false
                resetScanner()
            },
            title = { Text("Already Imported") },
            text = {
                Column {
                    Text("You've already imported this QR code on $importedDateStr.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Import again?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDuplicateDialog = false
                        pendingEnvelope?.let { envelope ->
                            continueWithEnvelope(envelope)
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDuplicateDialog = false
                        resetScanner()
                    }
                ) {
                    Text("No")
                }
            }
        )
    }

    // Age warning dialog
    if (showAgeWarningDialog && generatedDate != null) {
        AlertDialog(
            onDismissRequest = {
                showAgeWarningDialog = false
                resetScanner()
            },
            title = { Text("Old QR Code") },
            text = {
                Column {
                    Text("This QR code was generated on $generatedDate.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Data may be outdated. Continue?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAgeWarningDialog = false
                        pendingEnvelope?.let { envelope ->
                            continueWithEnvelope(envelope)
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAgeWarningDialog = false
                        resetScanner()
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}
