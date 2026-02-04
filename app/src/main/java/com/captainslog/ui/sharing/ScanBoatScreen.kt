package com.captainslog.ui.sharing

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.captainslog.database.AppDatabase
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.BoatImporter
import com.captainslog.sharing.BoatShareGenerator
import com.captainslog.sharing.ImportResult
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var scannedBoatName by remember { mutableStateOf("") }
    var scannedData by remember { mutableStateOf<com.captainslog.sharing.models.BoatShareData?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var scanError by remember { mutableStateOf<String?>(null) }
    var importResult by remember { mutableStateOf<ImportResult?>(null) }

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

    // Handle import confirmation
    fun handleImport() {
        scannedData?.let { data ->
            scope.launch {
                try {
                    isProcessing = true
                    val database = AppDatabase.getInstance(context)
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

/**
 * Camera preview composable with ML Kit barcode scanning.
 */
@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    var lastScannedValue by remember { mutableStateOf<String?>(null) }
    var lastScannedTime by remember { mutableStateOf(0L) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Image analysis for barcode scanning
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxy(
                                imageProxy,
                                barcodeScanner,
                                lastScannedValue,
                                lastScannedTime,
                                onQrCodeScanned = { value ->
                                    val now = System.currentTimeMillis()
                                    // Prevent duplicate scans within 2 seconds
                                    if (value != lastScannedValue || now - lastScannedTime > 2000) {
                                        lastScannedValue = value
                                        lastScannedTime = now
                                        onQrCodeScanned(value)
                                    }
                                }
                            )
                        }
                    }

                // Select back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }
}

/**
 * Process an image proxy for barcode detection.
 */
@androidx.camera.core.ExperimentalGetImage
private fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    lastScannedValue: String?,
    lastScannedTime: Long,
    onQrCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    if (barcode.format == Barcode.FORMAT_QR_CODE) {
                        barcode.rawValue?.let { value ->
                            onQrCodeScanned(value)
                        }
                    }
                }
            }
            .addOnFailureListener {
                // Ignore failures silently
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
