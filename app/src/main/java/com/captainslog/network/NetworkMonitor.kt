package com.captainslog.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.*
import com.captainslog.connection.ConnectionManager

/**
 * Monitors network connectivity and provides real-time connection status
 */
class NetworkMonitor(private val context: Context) {
    
    companion object {
        private const val TAG = "NetworkMonitor"
        
        @Volatile
        private var INSTANCE: NetworkMonitor? = null
        
        fun getInstance(context: Context): NetworkMonitor {
            return INSTANCE ?: synchronized(this) {
                val instance = NetworkMonitor(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    // Connection state flows
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private val _isOnWiFi = MutableStateFlow(false)
    val isOnWiFi: StateFlow<Boolean> = _isOnWiFi.asStateFlow()
    
    private val _isOnMobileData = MutableStateFlow(false)
    val isOnMobileData: StateFlow<Boolean> = _isOnMobileData.asStateFlow()
    
    private val _connectionType = MutableStateFlow(ConnectionType.NONE)
    val connectionType: StateFlow<ConnectionType> = _connectionType.asStateFlow()

    private val _isServerReachable = MutableStateFlow(false)
    val isServerReachable: StateFlow<Boolean> = _isServerReachable.asStateFlow()

    // Callback for connection changes
    var onConnectionChanged: ((Boolean, ConnectionType) -> Unit)? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var serverCheckJob: Job? = null
    
    enum class ConnectionType {
        NONE, WIFI, MOBILE_DATA, ETHERNET, OTHER
    }
    
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "Network available: $network")
            updateConnectionState()
        }
        
        override fun onLost(network: Network) {
            Log.d(TAG, "Network lost: $network")
            updateConnectionState()
        }
        
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            Log.d(TAG, "Network capabilities changed: $network")
            updateConnectionState()
        }
    }
    
    init {
        // Initial state check
        updateConnectionState()
        
        // Register network callback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            Log.d(TAG, "Network callback registered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register network callback", e)
        }
    }
    
    /**
     * Update connection state based on current network status
     */
    private fun updateConnectionState() {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
        
        val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val isWiFi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val isMobile = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        val isEthernet = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
        
        val connectionType = when {
            !hasInternet -> ConnectionType.NONE
            isWiFi -> ConnectionType.WIFI
            isMobile -> ConnectionType.MOBILE_DATA
            isEthernet -> ConnectionType.ETHERNET
            else -> ConnectionType.OTHER
        }
        
        // Update state flows
        _isConnected.value = hasInternet
        _isOnWiFi.value = isWiFi
        _isOnMobileData.value = isMobile
        _connectionType.value = connectionType
        
        // Notify callback
        onConnectionChanged?.invoke(hasInternet, connectionType)

        Log.d(TAG, "Connection state updated: connected=$hasInternet, type=$connectionType")
        checkServerReachability()
    }

    /**
     * Check if the backend server is reachable by calling the health endpoint.
     * Only runs when internet connectivity is available.
     */
    private fun checkServerReachability() {
        serverCheckJob?.cancel()
        if (!_isConnected.value) {
            _isServerReachable.value = false
            return
        }
        serverCheckJob = scope.launch {
            try {
                val connectionManager = ConnectionManager.getInstance(context)
                val (localOk, remoteOk) = connectionManager.testConnections()
                _isServerReachable.value = localOk || remoteOk
            } catch (e: Exception) {
                Log.w(TAG, "Server reachability check failed", e)
                _isServerReachable.value = false
            }
        }
    }
    
    /**
     * Get current connection status synchronously
     */
    fun getCurrentConnectionStatus(): ConnectionStatus {
        return ConnectionStatus(
            isConnected = _isConnected.value,
            isOnWiFi = _isOnWiFi.value,
            isOnMobileData = _isOnMobileData.value,
            connectionType = _connectionType.value,
            isServerReachable = _isServerReachable.value
        )
    }
    
    /**
     * Check if photos can be uploaded (WiFi only)
     */
    fun canUploadPhotos(): Boolean {
        return _isOnWiFi.value
    }
    
    /**
     * Check if data can be synced (any internet connection)
     */
    fun canSyncData(): Boolean {
        return _isConnected.value
    }

    /**
     * Mark server as reachable (called when sync succeeds, proving server connectivity)
     */
    fun reportServerReachable() {
        _isServerReachable.value = true
    }

    /**
     * Unregister network callback (call in onDestroy)
     */
    fun cleanup() {
        try {
            scope.cancel()
            connectivityManager.unregisterNetworkCallback(networkCallback)
            Log.d(TAG, "Network callback unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unregister network callback", e)
        }
    }
}

/**
 * Data class representing current connection status
 */
data class ConnectionStatus(
    val isConnected: Boolean,
    val isOnWiFi: Boolean,
    val isOnMobileData: Boolean,
    val connectionType: NetworkMonitor.ConnectionType,
    val isServerReachable: Boolean
)