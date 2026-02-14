package com.captainslog.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.captainslog.backup.BackupManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.BuildConfig
import com.captainslog.database.AppDatabase
import dagger.hilt.android.EntryPointAccessors
import com.captainslog.di.DatabaseModule
import com.captainslog.ui.components.BreadcrumbItem
import com.captainslog.sync.ConflictLogger
import com.captainslog.ui.settings.SyncSettingsScreen
import com.captainslog.viewmodel.TripTrackingViewModel
import com.captainslog.mode.AppModeManager
import com.captainslog.mode.AppMode
import com.captainslog.security.SecurePreferences
import com.captainslog.ui.auth.LoginViewModel
import kotlinx.coroutines.launch

/**
 * Main settings screen with navigation to various settings sections
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: TripTrackingViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    onNotesClick: () -> Unit = {},
    onTodosClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onBreadcrumbChanged: (List<BreadcrumbItem>, (() -> Unit)?) -> Unit = { _, _ -> },
    onConnectToServer: () -> Unit = {},
    database: AppDatabase,
    appModeManager: AppModeManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showSyncSettings by remember { mutableStateOf(false) }
    var showBoatManagement by remember { mutableStateOf(false) }
    var showNauticalSettings by remember { mutableStateOf(false) }
    var showShareBoat by remember { mutableStateOf(false) }
    var showScanBoat by remember { mutableStateOf(false) }
    var selectedBoatId by remember { mutableStateOf<String?>(null) }
    var isSyncing by remember { mutableStateOf(false) }
    var conflictLogs by remember { mutableStateOf("No conflicts logged") }
    var showDisconnectDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    val conflictLogger = remember { ConflictLogger(context) }
    val securePreferences = remember { SecurePreferences(context) }
    val currentMode by appModeManager.currentMode.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                isExporting = true
                try {
                    val json = BackupManager.exportToJson(database)
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(json.toByteArray())
                    }
                    Toast.makeText(context, "Backup exported successfully", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    isExporting = false
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
            showImportConfirmDialog = true
        }
    }

    // Load conflict logs
    LaunchedEffect(Unit) {
        conflictLogs = conflictLogger.getConflictLogs()
    }

    // Report breadcrumbs based on current sub-screen
    LaunchedEffect(showBoatManagement, showSyncSettings, showNauticalSettings, showShareBoat, showScanBoat) {
        val crumbs = when {
            showShareBoat -> listOf(BreadcrumbItem("Manage Boats"), BreadcrumbItem("Share Boat"))
            showScanBoat -> listOf(BreadcrumbItem("Manage Boats"), BreadcrumbItem("Scan Boat QR"))
            showBoatManagement -> listOf(BreadcrumbItem("Manage Boats"))
            showSyncSettings -> listOf(BreadcrumbItem("Sync Settings"))
            showNauticalSettings -> listOf(BreadcrumbItem("Nautical Data"))
            else -> emptyList()
        }
        val backToRoot: (() -> Unit)? = when {
            showShareBoat -> { { showShareBoat = false; selectedBoatId = null } }
            showScanBoat -> { { showScanBoat = false } }
            showBoatManagement || showSyncSettings || showNauticalSettings -> {
                { showBoatManagement = false; showSyncSettings = false; showNauticalSettings = false }
            }
            else -> null
        }
        onBreadcrumbChanged(crumbs, backToRoot)
    }

    if (showShareBoat && selectedBoatId != null) {
        com.captainslog.ui.sharing.ShareBoatScreen(
            boatId = selectedBoatId!!,
            onBack = {
                showShareBoat = false
                selectedBoatId = null
            },
            modifier = modifier,
            database = database
        )
    } else if (showScanBoat) {
        com.captainslog.ui.sharing.ScanBoatScreen(
            onBack = {
                showScanBoat = false
            },
            onBoatImported = { boatId ->
                // After successful scan, go back to boat management
                showScanBoat = false
            },
            modifier = modifier,
            database = database
        )
    } else if (showNauticalSettings) {
        NauticalSettingsScreen(modifier = modifier)
    } else if (showBoatManagement) {
        // Simply use the original BoatListScreen - it works fine, just needs proper space
        com.captainslog.ui.boats.BoatListScreen(
            modifier = modifier,
            database = database,
            onShareBoat = { boatId ->
                selectedBoatId = boatId
                showShareBoat = true
            },
            onScanBoatQR = {
                showScanBoat = true
            }
        )
    } else if (showSyncSettings) {
        SyncSettingsScreen(
            conflictLogs = conflictLogs,
            isSyncing = isSyncing,
            onTriggerSync = {
                scope.launch {
                    isSyncing = true
                    // Trigger sync via WorkManager
                    androidx.work.OneTimeWorkRequestBuilder<com.captainslog.sync.TripSyncWorker>()
                        .build()
                        .let { workRequest ->
                            androidx.work.WorkManager.getInstance(context)
                                .enqueue(workRequest)
                        }
                    // Wait a bit for sync to complete
                    kotlinx.coroutines.delay(2000)
                    isSyncing = false
                }
            },
            onClearLogs = {
                conflictLogger.clearLogs()
                conflictLogs = "No conflicts logged"
            }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Boat Management Section
            SettingsSection(title = "Boats") {
                SettingsItem(
                    icon = Icons.Filled.List,
                    title = "Manage Boats",
                    subtitle = "Add, enable/disable, and set active boat",
                    onClick = { showBoatManagement = true }
                )
            }

            Divider()

            // Nautical Data Section
            SettingsSection(title = "Nautical Data") {
                SettingsItem(
                    icon = Icons.Filled.Place,
                    title = "Nautical Data Providers",
                    subtitle = "Configure chart overlays, tides, AIS, and weather sources",
                    onClick = { showNauticalSettings = true }
                )
            }

            Divider()

            // Sync Settings Section
            SettingsSection(title = "Sync") {
                SettingsItem(
                    icon = Icons.Filled.Refresh,
                    title = "Sync Settings",
                    subtitle = "Manage trip synchronization and view conflict logs",
                    onClick = { showSyncSettings = true }
                )
            }

            Divider()

            // Backup & Restore Section
            SettingsSection(title = "Backup & Restore") {
                SettingsItem(
                    icon = Icons.Filled.Cloud,
                    title = "Export Backup",
                    subtitle = if (isExporting) "Exporting..." else "Save all data as a JSON file",
                    onClick = if (isExporting) null else {
                        {
                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                            exportLauncher.launch("captains_log_backup_$timestamp.json")
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Filled.Refresh,
                    title = "Import Backup",
                    subtitle = if (isImporting) "Importing..." else "Restore data from a JSON backup file",
                    onClick = if (isImporting) null else {
                        { importLauncher.launch(arrayOf("application/json")) }
                    }
                )
                Text(
                    text = "Note: Photos are not included in backups. Your data is also automatically backed up to Google Drive.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Divider()

            // Server Section
            SettingsSection(title = "Server") {
                when (currentMode) {
                    AppMode.STANDALONE -> {
                        SettingsItem(
                            icon = Icons.Filled.Cloud,
                            title = "Connect to Server",
                            subtitle = "Enable cloud sync and multi-device access",
                            onClick = onConnectToServer
                        )
                    }
                    AppMode.CONNECTED -> {
                        val serverUrl = securePreferences.remoteUrl ?: "Unknown server"
                        val username = securePreferences.username ?: "Unknown user"

                        SettingsItem(
                            icon = Icons.Filled.Cloud,
                            title = serverUrl,
                            subtitle = "Logged in as $username",
                            onClick = null
                        )

                        SettingsItem(
                            icon = Icons.Filled.CloudOff,
                            title = "Disconnect",
                            subtitle = "Switch back to standalone mode",
                            onClick = { showDisconnectDialog = true }
                        )
                    }
                }
            }

            Divider()

            // Account Section - Only show in CONNECTED mode
            if (currentMode == AppMode.CONNECTED) {
                SettingsSection(title = "Account") {
                    SettingsItem(
                        icon = Icons.Default.ExitToApp,
                        title = "Sign Out",
                        subtitle = "Sign out of your account",
                        onClick = {
                            scope.launch {
                                // Use LoginViewModel to properly logout
                                loginViewModel.logout()

                                // Invoke sign-out callback
                                onSignOut()
                            }
                        }
                    )
                }

                Divider()
            }

            // About Section
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Privacy Policy",
                    subtitle = "How your data is handled",
                    onClick = { showPrivacyPolicy = true }
                )
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "App Version",
                    subtitle = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    onClick = null
                )
            }
        }
    }

    // Import confirmation dialog
    if (showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showImportConfirmDialog = false
                pendingImportUri = null
            },
            title = { Text("Import Backup") },
            text = {
                Text("This will merge the backup data with your existing data. Matching records will be overwritten. Continue?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportConfirmDialog = false
                        val uri = pendingImportUri ?: return@TextButton
                        pendingImportUri = null
                        scope.launch {
                            isImporting = true
                            try {
                                val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    inputStream.bufferedReader().readText()
                                } ?: throw Exception("Could not read file")
                                val stats = BackupManager.importFromJson(database, json)
                                val total = stats.trips + stats.boats + stats.notes + stats.gpsPoints +
                                    stats.crewMembers + stats.photos + stats.todoLists + stats.todoItems +
                                    stats.maintenanceTemplates + stats.maintenanceEvents + stats.markedLocations
                                Toast.makeText(context, "Imported $total records successfully", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isImporting = false
                            }
                        }
                    }
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImportConfirmDialog = false
                    pendingImportUri = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Privacy Policy dialog
    if (showPrivacyPolicy) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicy = false },
            title = { Text("Privacy Policy") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = """Captain's Log Privacy Policy
Last updated: February 2026

Your privacy matters. Here's how Captain's Log handles your data:

Location Data
GPS data is collected solely for trip tracking purposes. All location data is stored locally on your device.

Photos
Photos you capture are stored locally on your device and are never transmitted unless you explicitly sync with a server.

Bluetooth
Bluetooth is used only for optional sensor connections (e.g., wind instruments).

Camera
The camera is used for QR code scanning and trip photos.

Network Access
Network access is used only for optional server synchronization, which is always user-initiated.

No Tracking
Captain's Log contains no third-party analytics, advertising SDKs, or tracking software.

Your Data Stays Yours
Your data is never sold or shared with third parties. All data remains on your device unless you explicitly connect to a server for synchronization.

Contact
For questions about this policy, visit boat.jware.dev.""",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicy = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Disconnect confirmation dialog
    if (showDisconnectDialog) {
        AlertDialog(
            onDismissRequest = { showDisconnectDialog = false },
            title = { Text("Disconnect from Server") },
            text = {
                Text("Are you sure you want to disconnect from the server? You will switch back to standalone mode and will need to reconnect to sync your data.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            // Clear server connection
                            securePreferences.remoteUrl = null
                            securePreferences.localUrl = null
                            securePreferences.jwtToken = null
                            securePreferences.username = null

                            // Refresh app mode
                            appModeManager.refresh()

                            showDisconnectDialog = false
                        }
                    }
                ) {
                    Text("Disconnect")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisconnectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (onClick != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Navigate",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}




