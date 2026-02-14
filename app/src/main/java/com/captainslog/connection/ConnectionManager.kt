package com.captainslog.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.captainslog.network.ApiService
import com.captainslog.security.CertificatePinnerBuilder
import com.captainslog.security.SecurePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val securePreferences = SecurePreferences(context)
    private var localApiService: ApiService? = null
    private var remoteApiService: ApiService? = null
    private var currentConnectionType: ConnectionType = ConnectionType.NONE
    
    // Callback for token expiration
    var onTokenExpired: (() -> Unit)? = null

    enum class ConnectionType {
        LOCAL, REMOTE, NONE
    }

    data class ConnectionConfig(
        val localUrl: String?,
        val remoteUrl: String,
        val jwtToken: String?,
        val localCertPin: String?,
        val remoteCertPin: String
    )

    fun initialize() {
        val config = getConnectionConfig() ?: return

        // Build certificate pinner
        val certificatePinner = CertificatePinnerBuilder.build(
            localUrl = config.localUrl,
            localPin = config.localCertPin,
            remoteUrl = config.remoteUrl,
            remotePin = config.remoteCertPin
        )

        // Create OkHttp client with certificate pinning and JWT token authentication
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (com.captainslog.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()
            
            // Add Authorization header if JWT token is available
            val request = if (!config.jwtToken.isNullOrEmpty()) {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer ${config.jwtToken}")
                    .build()
            } else {
                originalRequest
            }
            
            val response = chain.proceed(request)
            
            // Check for 401 Unauthorized (token expired or invalid)
            if (response.code == 401 && !config.jwtToken.isNullOrEmpty()) {
                android.util.Log.w("ConnectionManager", "Token expired or invalid (401), clearing session")
                // Clear token on 401 response
                securePreferences.jwtToken = null
                securePreferences.username = null
                // Notify listeners
                onTokenExpired?.invoke()
            }
            
            response
        }

        // Local client with short timeout
        if (!config.localUrl.isNullOrEmpty()) {
            val localClient = OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build()

            localApiService = Retrofit.Builder()
                .baseUrl(config.localUrl)
                .client(localClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }

        // Remote client with normal timeout
        val remoteClient = OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        remoteApiService = Retrofit.Builder()
            .baseUrl(config.remoteUrl)
            .client(remoteClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Check if server is configured (remoteUrl is set).
     * Returns true if the app has a server configuration, false if in standalone mode.
     */
    fun isServerConfigured(): Boolean {
        return securePreferences.remoteUrl != null
    }

    /**
     * Get the appropriate API service based on connection availability.
     * Returns local service if configured and on WiFi, otherwise remote.
     * Throws if no server is configured (standalone mode).
     * Actual fallback happens at the call site when the request fails.
     */
    fun getApiService(): ApiService {
        if (!isServerConfigured()) {
            throw IllegalStateException("API service not initialized - server not configured (standalone mode)")
        }

        if (localApiService != null && isOnWiFi()) {
            currentConnectionType = ConnectionType.LOCAL
            return localApiService!!
        }
        currentConnectionType = ConnectionType.REMOTE
        return remoteApiService ?: throw IllegalStateException("Remote API service not initialized")
    }

    /**
     * Get the API service, returning null if not configured.
     * Use this for standalone-aware code that needs to check if server is available.
     */
    fun getApiServiceOrNull(): ApiService? {
        if (!isServerConfigured()) {
            currentConnectionType = ConnectionType.NONE
            return null
        }

        if (localApiService != null && isOnWiFi()) {
            currentConnectionType = ConnectionType.LOCAL
            return localApiService
        }
        currentConnectionType = ConnectionType.REMOTE
        return remoteApiService
    }

    /**
     * Get the remote API service directly, bypassing local.
     * Returns null if no server is configured (standalone mode).
     */
    fun getRemoteApiService(): ApiService? {
        if (!isServerConfigured()) {
            currentConnectionType = ConnectionType.NONE
            return null
        }

        currentConnectionType = ConnectionType.REMOTE
        return remoteApiService
    }

    /**
     * Get the remote API service, throwing an exception if not configured.
     * Use this for callers that require server connectivity.
     */
    fun getRemoteApiServiceOrThrow(): ApiService {
        return getRemoteApiService() ?: throw IllegalStateException("API service not initialized - server not configured")
    }

    fun getCurrentConnectionType(): ConnectionType = currentConnectionType

    /**
     * Test connection to verify configuration
     * Returns a pair of (localSuccess, remoteSuccess)
     */
    suspend fun testConnections(): Pair<Boolean, Boolean> {
        var localSuccess = false
        var remoteSuccess = false

        // Test local connection if configured
        if (localApiService != null) {
            try {
                // Use health endpoint which doesn't require auth
                val response = localApiService!!.healthCheck()
                localSuccess = response.isSuccessful
                android.util.Log.d("ConnectionManager", "Local connection test: ${response.code()}")
            } catch (e: Exception) {
                android.util.Log.w("ConnectionManager", "Local connection test failed", e)
                localSuccess = false
            }
        }

        // Test remote connection if configured
        if (remoteApiService != null) {
            try {
                // Use health endpoint which doesn't require auth
                val response = remoteApiService!!.healthCheck()
                remoteSuccess = response.isSuccessful
                android.util.Log.d("ConnectionManager", "Remote connection test: ${response.code()}")
            } catch (e: Exception) {
                android.util.Log.e("ConnectionManager", "Remote connection test failed", e)
                remoteSuccess = false
            }
        }

        return Pair(localSuccess, remoteSuccess)
    }

    fun isOnWiFi(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun isOnMobileData(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun hasInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun getConnectionConfig(): ConnectionConfig? {
        val remoteUrl = securePreferences.remoteUrl ?: return null
        
        // In debug builds, certificate pins are optional
        val remoteCertPin = if (com.captainslog.BuildConfig.REQUIRE_CERT_PINNING) {
            securePreferences.remoteCertPin ?: return null
        } else {
            securePreferences.remoteCertPin ?: ""
        }

        return ConnectionConfig(
            localUrl = securePreferences.localUrl,
            remoteUrl = remoteUrl,
            jwtToken = securePreferences.jwtToken,
            localCertPin = securePreferences.localCertPin,
            remoteCertPin = remoteCertPin
        )
    }

}
