package com.captainslog.viewmodel

import android.app.Application
import com.captainslog.database.entities.MarkedLocationEntity
import com.captainslog.repository.MarkedLocationRepository
import com.captainslog.repository.MarkedLocationWithDistance
import com.captainslog.repository.TripRepository
import com.captainslog.nautical.NauticalSettingsManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var application: Application
    private lateinit var tripRepository: TripRepository
    private lateinit var markedLocationRepository: MarkedLocationRepository
    private lateinit var nauticalSettingsManager: NauticalSettingsManager
    private lateinit var viewModel: MapViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        tripRepository = mockk(relaxed = true)
        markedLocationRepository = mockk(relaxed = true)
        nauticalSettingsManager = mockk(relaxed = true)

        // Mock init calls - loadTrips and loadMarkedLocations
        every { tripRepository.getAllTrips() } returns flowOf(emptyList())
        every { markedLocationRepository.getAllMarkedLocations() } returns flowOf(emptyList())
        coEvery { markedLocationRepository.syncMarkedLocationsFromApi() } returns Result.success(Unit)

        viewModel = MapViewModel(application, tripRepository, markedLocationRepository, nauticalSettingsManager)
    }

    // --- initial state ---

    @Test
    fun `initial uiState has default values`() {
        val state = viewModel.uiState.value
        assertTrue(state.trips.isEmpty())
        assertTrue(state.markedLocations.isEmpty())
        assertNull(state.selectedMarkedLocation)
        assertNull(state.currentLatitude)
        assertNull(state.currentLongitude)
        assertTrue(state.filter.showTrips)
        assertTrue(state.filter.showMarkedLocations)
        assertNull(state.error)
    }

    // --- updateCurrentLocation ---

    @Test
    fun `updateCurrentLocation updates state`() = runTest {
        viewModel.updateCurrentLocation(37.7749, -122.4194)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(37.7749, state.currentLatitude!!, 0.001)
        assertEquals(-122.4194, state.currentLongitude!!, 0.001)
    }

    // --- selectMarkedLocation ---

    @Test
    fun `selectMarkedLocation updates state`() {
        val location = mockk<MarkedLocationEntity>(relaxed = true)

        viewModel.selectMarkedLocation(location)

        assertEquals(location, viewModel.uiState.value.selectedMarkedLocation)
    }

    // --- clearSelectedMarkedLocation ---

    @Test
    fun `clearSelectedMarkedLocation clears selection`() {
        val location = mockk<MarkedLocationEntity>(relaxed = true)
        viewModel.selectMarkedLocation(location)

        viewModel.clearSelectedMarkedLocation()

        assertNull(viewModel.uiState.value.selectedMarkedLocation)
    }

    // --- updateFilter ---

    @Test
    fun `updateFilter updates state`() = runTest {
        val newFilter = MapFilter(showTrips = false, showMarkedLocations = true)

        viewModel.updateFilter(newFilter)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.filter.showTrips)
        assertTrue(viewModel.uiState.value.filter.showMarkedLocations)
    }

    // --- createMarkedLocation ---

    @Test
    fun `createMarkedLocation success does not set error`() = runTest {
        coEvery {
            markedLocationRepository.createMarkedLocation(
                name = "Anchorage",
                latitude = 37.0,
                longitude = -122.0,
                category = "anchorage",
                notes = null,
                tags = listOf("calm")
            )
        } returns Result.success(mockk(relaxed = true))

        viewModel.createMarkedLocation("Anchorage", 37.0, -122.0, "anchorage", "", listOf("calm"))

        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `createMarkedLocation failure sets error`() = runTest {
        coEvery {
            markedLocationRepository.createMarkedLocation(any(), any(), any(), any(), any(), any())
        } returns Result.failure(Exception("Validation error"))

        viewModel.createMarkedLocation("Bad", 0.0, 0.0, "other", "", emptyList())

        advanceUntilIdle()

        assertEquals("Validation error", viewModel.uiState.value.error)
    }

    @Test
    fun `createMarkedLocation exception sets error`() = runTest {
        coEvery {
            markedLocationRepository.createMarkedLocation(any(), any(), any(), any(), any(), any())
        } throws RuntimeException("Network error")

        viewModel.createMarkedLocation("Bad", 0.0, 0.0, "other", "", emptyList())

        advanceUntilIdle()

        assertEquals("Network error", viewModel.uiState.value.error)
    }

    // --- searchMarkedLocations ---

    @Test
    fun `searchMarkedLocations with blank query reloads all`() = runTest {
        viewModel.searchMarkedLocations("")

        advanceUntilIdle()

        // Should call loadMarkedLocations which uses getAllMarkedLocations
        // No error should be set
        assertNull(viewModel.uiState.value.error)
    }

    // --- getNearbyMarkedLocations ---

    @Test
    fun `getNearbyMarkedLocations without location does nothing`() = runTest {
        // No current location set
        viewModel.getNearbyMarkedLocations(1000.0)

        advanceUntilIdle()

        // Should not crash or set error
    }

    @Test
    fun `getNearbyMarkedLocations with location calls repository`() = runTest {
        viewModel.updateCurrentLocation(37.0, -122.0)
        advanceUntilIdle()

        val locations = listOf(
            MarkedLocationWithDistance(
                location = mockk(relaxed = true),
                distanceMeters = 500.0
            )
        )
        every {
            markedLocationRepository.getNearbyMarkedLocations(37.0, -122.0, 1000.0)
        } returns flowOf(locations)

        viewModel.getNearbyMarkedLocations(1000.0)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.markedLocations.size)
    }

    // --- toggleNauticalLayerVisibility ---

    @Test
    fun `toggleNauticalLayerVisibility toggles from default true to false`() {
        viewModel.toggleNauticalLayerVisibility("noaa-coops")

        assertFalse(viewModel.uiState.value.nauticalLayerVisibility["noaa-coops"]!!)
    }

    @Test
    fun `toggleNauticalLayerVisibility toggles back to true`() {
        viewModel.toggleNauticalLayerVisibility("noaa-coops")
        viewModel.toggleNauticalLayerVisibility("noaa-coops")

        assertTrue(viewModel.uiState.value.nauticalLayerVisibility["noaa-coops"]!!)
    }

    // --- isNauticalLayerVisible ---

    @Test
    fun `isNauticalLayerVisible returns false when provider disabled`() {
        every { nauticalSettingsManager.isEnabled("aisstream") } returns false

        assertFalse(viewModel.isNauticalLayerVisible("aisstream"))
    }

    @Test
    fun `isNauticalLayerVisible returns true when enabled and not toggled off`() {
        every { nauticalSettingsManager.isEnabled("noaa-coops") } returns true

        assertTrue(viewModel.isNauticalLayerVisible("noaa-coops"))
    }

    @Test
    fun `isNauticalLayerVisible returns false when toggled off`() {
        every { nauticalSettingsManager.isEnabled("noaa-coops") } returns true
        viewModel.toggleNauticalLayerVisibility("noaa-coops")

        assertFalse(viewModel.isNauticalLayerVisible("noaa-coops"))
    }
}
