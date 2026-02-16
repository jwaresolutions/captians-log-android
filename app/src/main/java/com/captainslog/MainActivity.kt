package com.captainslog

import android.content.Context
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


    private var permissionsGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "MainActivity onCreate")

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
    

    @Composable
    private fun MainApp() {
        val context = LocalContext.current
        val prefs = remember { context.getSharedPreferences("captains_log_prefs", Context.MODE_PRIVATE) }
        var showDisclaimer by remember { mutableStateOf(!prefs.getBoolean("beta_disclaimer_shown", false)) }

        if (showDisclaimer) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Early Access Beta") },
                text = {
                    Text("Thanks for trying Captain's Log! This is an early beta — things may change, break, or improve as development continues. Some features may become part of a future Pro upgrade, but your data will always be preserved. We appreciate your feedback!")
                },
                confirmButton = {
                    TextButton(onClick = {
                        prefs.edit().putBoolean("beta_disclaimer_shown", true).apply()
                        showDisclaimer = false
                    }) {
                        Text("Got It")
                    }
                }
            )
        }

        MainAppContent()
    }

    @Composable
    private fun MainAppContent() {
        MainNavigation()
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
