package com.captainslog.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.captainslog.security.SecurePreferences
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ConnectionManagerTest {
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var securePreferences: SecurePreferences
    private lateinit var connectionManager: ConnectionManager

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        connectivityManager = mockk(relaxed = true)
        securePreferences = mockk(relaxed = true)

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { context.applicationContext } returns context

        // Mock SecurePreferences constructor
        mockkConstructor(SecurePreferences::class)
        every { anyConstructed<SecurePreferences>().jwtToken } returns "test-jwt-token"
        every { anyConstructed<SecurePreferences>().remoteUrl } returns "https://remote.example.com"
        every { anyConstructed<SecurePreferences>().remoteCertPin } returns "remote-pin-hash"
        every { anyConstructed<SecurePreferences>().localUrl } returns "https://local.example.com"
        every { anyConstructed<SecurePreferences>().localCertPin } returns "local-pin-hash"

        connectionManager = ConnectionManager(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isOnWiFi returns true when connected to WiFi`() {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true

        assertTrue(connectionManager.isOnWiFi())
    }

    @Test
    fun `isOnWiFi returns false when not connected to WiFi`() {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false

        assertFalse(connectionManager.isOnWiFi())
    }

    @Test
    fun `isOnMobileData returns true when connected to cellular`() {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true

        assertTrue(connectionManager.isOnMobileData())
    }

    @Test
    fun `hasInternetConnection returns true when internet is available`() {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        assertTrue(connectionManager.hasInternetConnection())
    }

    @Test
    fun `hasInternetConnection returns false when no network`() {
        every { connectivityManager.activeNetwork } returns null

        assertFalse(connectionManager.hasInternetConnection())
    }

    @Test
    fun `getCurrentConnectionType returns NONE initially`() {
        assertEquals(ConnectionManager.ConnectionType.NONE, connectionManager.getCurrentConnectionType())
    }
}
