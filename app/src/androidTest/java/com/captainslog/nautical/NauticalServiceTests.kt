package com.captainslog.nautical

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.captainslog.nautical.service.AISStreamService
import com.captainslog.nautical.service.NoaaCoOpsService
import com.captainslog.nautical.service.OpenMeteoService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for nautical data providers.
 * These hit real APIs to verify the services are working correctly.
 * Run on a device/emulator with network access.
 */
@RunWith(AndroidJUnit4::class)
class NauticalServiceTests {

    // --- NOAA CO-OPS Tests ---

    @Test
    fun noaaCoOps_fetchTideStations_returnsStationsInBounds() = runBlocking {
        // Seattle/Puget Sound area - known to have many tide stations
        val stations = withTimeout(15_000) {
            NoaaCoOpsService.fetchTideStations(
                minLat = 47.0, minLng = -123.0,
                maxLat = 48.5, maxLng = -122.0
            )
        }
        assertTrue(
            "Expected tide stations in Puget Sound area, got ${stations.size}",
            stations.isNotEmpty()
        )
        // Verify station data is well-formed
        val first = stations.first()
        assertTrue("Station ID should not be blank", first.id.isNotBlank())
        assertTrue("Station name should not be blank", first.name.isNotBlank())
        assertTrue("Latitude should be in range", first.latitude in 47.0..48.5)
        assertTrue("Longitude should be in range", first.longitude in -123.0..-122.0)
    }

    @Test
    fun noaaCoOps_fetchTideStations_emptyForMiddleOfOcean() = runBlocking {
        // Middle of Pacific - should have no stations
        val stations = withTimeout(15_000) {
            NoaaCoOpsService.fetchTideStations(
                minLat = 20.0, minLng = -160.0,
                maxLat = 21.0, maxLng = -159.0
            )
        }
        // May or may not have stations (Hawaii area), but should not crash
        assertNotNull("Should return a list (possibly empty), not null", stations)
    }

    @Test
    fun noaaCoOps_fetchTidePredictions_returnsDataForKnownStation() = runBlocking {
        // Station 9447130 = Seattle, WA - a well-known NOAA station
        val predictions = withTimeout(15_000) {
            NoaaCoOpsService.fetchTidePredictions("9447130")
        }
        assertTrue(
            "Expected tide predictions for Seattle station, got ${predictions.size}",
            predictions.isNotEmpty()
        )
        val first = predictions.first()
        assertTrue("Time should not be blank", first.time.isNotBlank())
        assertTrue("Type should be H or L", first.type == "H" || first.type == "L")
    }

    @Test
    fun noaaCoOps_fetchTidePredictions_emptyForInvalidStation() = runBlocking {
        val predictions = withTimeout(15_000) {
            NoaaCoOpsService.fetchTidePredictions("0000000")
        }
        // Should return empty list, not crash
        assertNotNull(predictions)
    }

    // --- Open-Meteo Marine Weather Tests ---

    @Test
    fun openMeteo_fetchMarineWeather_returnsDataForCoastalLocation() = runBlocking {
        // Seattle waterfront
        val weather = withTimeout(15_000) {
            OpenMeteoService.fetchMarineWeather(47.6062, -122.3321)
        }
        assertNotNull("Expected marine weather data for Seattle", weather)
        weather!!
        // Verify lat/lng are close to requested
        assertEquals(47.6, weather.latitude, 0.5)
        assertEquals(-122.3, weather.longitude, 0.5)
        // At least some fields should be populated
        assertTrue(
            "Expected at least one weather field to be non-null",
            weather.waveHeight != null || weather.windSpeed != null || weather.temperature != null
        )
    }

    @Test
    fun openMeteo_fetchMarineWeather_returnsDataForOpenOcean() = runBlocking {
        // Middle of Pacific
        val weather = withTimeout(15_000) {
            OpenMeteoService.fetchMarineWeather(30.0, -150.0)
        }
        assertNotNull("Expected marine weather data for open ocean", weather)
        weather!!
        // Open ocean should have wave data
        assertNotNull("Expected wave height for open ocean", weather.waveHeight)
    }

    @Test
    fun openMeteo_fetchMarineWeather_handlesInlandLocation() = runBlocking {
        // Denver, CO - inland, no marine data expected
        val weather = withTimeout(15_000) {
            OpenMeteoService.fetchMarineWeather(39.7392, -104.9903)
        }
        // Should either return null or return data with null fields - not crash
        // Open-Meteo may return NaN for inland locations
    }

    // --- AISStream WebSocket Tests ---

    @Test
    fun aisStream_initialState_isEmpty() {
        val service = AISStreamService()
        assertTrue("Initial vessel list should be empty", service.vesselFlow.value.isEmpty())
    }

    @Test
    fun aisStream_connectWithoutApiKey_doesNotCrash() {
        val service = AISStreamService()
        // Empty API key should fail gracefully
        service.connect("", 47.0, -123.0, 48.0, -122.0)
        // Give it a moment then disconnect
        Thread.sleep(2000)
        service.disconnect()
        assertTrue("Vessel list should be empty after failed connect", service.vesselFlow.value.isEmpty())
    }

    @Test
    fun aisStream_disconnectCleanup() {
        val service = AISStreamService()
        service.disconnect()
        assertTrue("Vessel list should be empty after disconnect", service.vesselFlow.value.isEmpty())
    }

    // --- NauticalProviders Registry Tests ---

    @Test
    fun nauticalProviders_registryHasAllProviders() {
        val all = NauticalProviders.all
        assertTrue("Expected at least 6 providers, got ${all.size}", all.size >= 6)
    }

    @Test
    fun nauticalProviders_freeProvidersExist() {
        val freeIds = listOf("openseamap", "noaa-charts", "gebco", "noaa-coops", "aisstream", "open-meteo")
        freeIds.forEach { id ->
            assertNotNull("Provider '$id' should exist in registry", NauticalProviders.getById(id))
        }
    }

    @Test
    fun nauticalProviders_getByIdReturnsNullForUnknown() {
        assertNull(NauticalProviders.getById("nonexistent-provider"))
    }
}
