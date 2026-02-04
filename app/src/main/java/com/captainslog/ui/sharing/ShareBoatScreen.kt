package com.captainslog.ui.sharing

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.BoatEntity
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.BoatShareGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Screen for sharing a boat via QR code.
 *
 * @param boatId The ID of the boat to share
 * @param onBack Callback when back button is pressed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBoatScreen(
    boatId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var boat by remember { mutableStateOf<BoatEntity?>(null) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Load boat and generate QR code
    LaunchedEffect(boatId) {
        try {
            isLoading = true
            error = null

            // Load boat from database
            val database = AppDatabase.getInstance(context)
            val loadedBoat = withContext(Dispatchers.IO) {
                database.boatDao().getBoatById(boatId)
            }

            if (loadedBoat == null) {
                error = "Boat not found"
                isLoading = false
                return@LaunchedEffect
            }

            boat = loadedBoat

            // Generate QR code
            val securePreferences = SecurePreferences(context)
            val generator = BoatShareGenerator(securePreferences)
            val bitmap = withContext(Dispatchers.Default) {
                generator.generateQrBitmap(loadedBoat, size = 512)
            }

            qrBitmap = bitmap
            isLoading = false
        } catch (e: Exception) {
            error = "Error generating QR code: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Boat") },
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
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
                qrBitmap != null && boat != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Boat name
                        Text(
                            text = boat!!.name,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )

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
                                    contentDescription = "QR Code for ${boat!!.name}",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Instructions
                        Text(
                            text = "Scan this code on another device to share the boat",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
