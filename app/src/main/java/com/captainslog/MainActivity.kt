package com.captainslog

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.captainslog.connection.ConnectionManager
import com.captainslog.mode.AppModeManager
import com.captainslog.network.NetworkMonitor
import com.captainslog.security.SecurePreferences
import com.captainslog.sync.OfflineChangeService
import com.captainslog.sync.SyncOrchestrator
import com.captainslog.ui.components.ConnectivityStatusBar
import com.captainslog.ui.theme.BoatTrackingTheme
import com.captainslog.ui.MainNavigation
import com.captainslog.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    @Inject lateinit var securePreferences: SecurePreferences
    @Inject lateinit var connectionManager: ConnectionManager
    @Inject lateinit var networkMonitor: NetworkMonitor
    @Inject lateinit var appModeManager: AppModeManager
    @Inject lateinit var syncOrchestrator: SyncOrchestrator
    @Inject lateinit var offlineChangeService: OfflineChangeService

    private var permissionsGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "MainActivity onCreate")

        // Log token expiration (no longer forces login screen)
        connectionManager.onTokenExpired = {
            Log.d(TAG, "Token expired - background sync will be unavailable until re-authentication")
        }

        // Check and request permissions
        checkAndRequestPermissions()

        setContent {
            BoatTrackingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (permissionsGranted) {
                        MainApp()
                    } else {
                        PermissionRequestScreen(
                            onPermissionsRequested = {
                                checkAndRequestPermissions()
                            }
                        )
                    }
                }
            }
        }
    }
    
    private fun checkAndRequestPermissions() {
        Log.d(TAG, "Checking permissions...")
        
        if (PermissionManager.hasAllRequiredPermissions(this)) {
            Log.d(TAG, "All permissions granted")
            permissionsGranted = true
        } else {
            Log.d(TAG, "Missing permissions, requesting...")
            val missingPermissions = PermissionManager.getMissingPermissions(this)
            Log.d(TAG, "Missing permissions: $missingPermissions")
            
            PermissionManager.requestAllPermissions(this)
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        Log.d(TAG, "Permission result: requestCode=$requestCode")
        
        when (requestCode) {
            PermissionManager.ALL_PERMISSIONS_REQUEST_CODE,
            PermissionManager.LOCATION_PERMISSION_REQUEST_CODE,
            PermissionManager.NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                
                val allGranted = grantResults.isNotEmpty() && 
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                
                Log.d(TAG, "All permissions granted: $allGranted")
                
                if (allGranted) {
                    permissionsGranted = true
                    Log.d(TAG, "Permissions granted - app can proceed")
                } else {
                    Log.w(TAG, "Some permissions denied")
                    // Check if we have the minimum required permissions
                    if (PermissionManager.hasLocationPermissions(this)) {
                        Log.d(TAG, "Location permissions granted - proceeding with limited functionality")
                        permissionsGranted = true
                    } else {
                        Log.e(TAG, "Critical permissions denied - cannot proceed")
                        permissionsGranted = false
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::networkMonitor.isInitialized) {
            networkMonitor.cleanup()
        }
    }

    @Composable
    private fun MainApp() {
        MainAppContent()
    }

    @Composable
    private fun MainAppContent(onSignOut: () -> Unit = {}) {
        val appMode by appModeManager.currentMode.collectAsState()
        val isConnectedMode = appMode == com.captainslog.mode.AppMode.CONNECTED

        val isConnected by networkMonitor.isConnected.collectAsState()
        val isServerReachable by networkMonitor.isServerReachable.collectAsState()
        val connectionType by networkMonitor.connectionType.collectAsState()

        val isSyncing by syncOrchestrator.isSyncing.collectAsState()
        val syncConflicts by syncOrchestrator.syncConflicts.collectAsState()
        val syncProgress by syncOrchestrator.syncProgress.collectAsState()

        // Trigger comprehensive sync on app startup and when network connects
        LaunchedEffect(Unit) {
            Log.d("MainActivity", "App started, app mode: $appMode")
            if (isConnectedMode && isConnected) {
                Log.d("MainActivity", "Connected mode with network available, starting immediate sync...")
                syncOrchestrator.syncAll()
            } else if (!isConnectedMode) {
                Log.d("MainActivity", "Standalone mode - sync disabled")
            } else {
                Log.d("MainActivity", "Connected mode but no network, will sync when connected")
            }
        }

        LaunchedEffect(isConnected, isConnectedMode) {
            if (isConnectedMode && isConnected) {
                Log.d("MainActivity", "Network connected in connected mode, starting comprehensive sync...")
                syncOrchestrator.syncAll()
            }
        }

        var offlineStatus by remember {
            mutableStateOf(com.captainslog.sync.OfflineStatus(false, 0, 0, null))
        }

        LaunchedEffect(Unit) {
            offlineStatus = offlineChangeService.getOfflineStatus()
        }

        var showSyncConflicts by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {
            if (isConnectedMode) {
                ConnectivityStatusBar(
                    isConnected = isConnected,
                    isServerReachable = isServerReachable,
                    connectionType = connectionType,
                    offlineStatus = offlineStatus,
                    isSyncing = isSyncing,
                    hasUnresolvedConflicts = syncConflicts.isNotEmpty(),
                    onSyncConflictClick = { showSyncConflicts = true }
                )
            }

            syncProgress?.let { progress ->
                LinearProgressIndicator(
                    progress = { progress.percentage / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = progress.message,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (showSyncConflicts) {
                com.captainslog.ui.sync.SyncConflictScreen(
                    conflicts = syncConflicts,
                    onBack = { showSyncConflicts = false },
                    onResolveConflict = { conflictId, useLocal ->
                        syncOrchestrator.resolveSyncConflict(conflictId, useLocal)
                    }
                )
            } else {
                MainNavigation(onSignOut = onSignOut)
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(
    onPermissionsRequested: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Permissions Required",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This app needs location permissions to track your boat trips and provide GPS functionality.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onPermissionsRequested,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant Permissions")
        }
    }
}
