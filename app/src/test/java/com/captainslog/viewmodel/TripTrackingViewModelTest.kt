package com.captainslog.viewmodel

import android.app.Application
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.TripDao
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.repository.TripRepository
import com.captainslog.repository.TripStatistics as RepoTripStatistics
import com.captainslog.sync.SyncOrchestrator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class TripTrackingViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var application: Application
    private lateinit var repository: TripRepository
    private lateinit var database: AppDatabase
    private lateinit var syncOrchestrator: SyncOrchestrator
    private lateinit var tripDao: TripDao
    private lateinit var viewModel: TripTrackingViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        database = mockk(relaxed = true)
        syncOrchestrator = mockk(relaxed = true)
        tripDao = mockk(relaxed = true)
        every { database.tripDao() } returns tripDao
        coEvery { tripDao.getActiveTrips() } returns emptyList()
        viewModel = TripTrackingViewModel(application, repository, database, syncOrchestrator)
    }

    // --- initial state ---

    @Test
    fun `initial state is not tracking`() {
        assertFalse(viewModel.isTracking.value)
    }

    @Test
    fun `initial currentTripId is null`() {
        assertNull(viewModel.currentTripId.value)
    }

    @Test
    fun `initial currentTrip is null`() {
        assertNull(viewModel.currentTrip.value)
    }

    @Test
    fun `initial errorMessage is null`() {
        assertNull(viewModel.errorMessage.value)
    }

    // --- getAllTrips ---

    @Test
    fun `getAllTrips returns flow from repository`() = runTest {
        val trips = listOf(
            TripEntity(boatId = "boat1", startTime = Date()),
            TripEntity(boatId = "boat2", startTime = Date())
        )
        every { repository.getAllTrips() } returns flowOf(trips)

        val result = viewModel.getAllTrips().first()

        assertEquals(2, result.size)
    }

    @Test
    fun `getAllTrips returns empty list when no trips`() = runTest {
        every { repository.getAllTrips() } returns flowOf(emptyList())

        val result = viewModel.getAllTrips().first()

        assertTrue(result.isEmpty())
    }

    // --- loadTrip ---

    @Test
    fun `loadTrip sets currentTrip from repository`() = runTest {
        val trip = TripEntity(id = "trip1", boatId = "boat1", startTime = Date())
        coEvery { repository.getTripById("trip1") } returns trip

        viewModel.loadTrip("trip1")

        assertEquals("trip1", viewModel.currentTrip.value?.id)
    }

    @Test
    fun `loadTrip with unknown id sets currentTrip to null`() = runTest {
        coEvery { repository.getTripById("unknown") } returns null

        viewModel.loadTrip("unknown")

        assertNull(viewModel.currentTrip.value)
    }

    // --- getGpsPointsForTrip ---

    @Test
    fun `getGpsPointsForTrip returns flow from repository`() = runTest {
        val points = listOf(
            GpsPointEntity(tripId = "trip1", latitude = 1.0, longitude = 2.0, timestamp = Date())
        )
        every { repository.getGpsPointsForTrip("trip1") } returns flowOf(points)

        val result = viewModel.getGpsPointsForTrip("trip1").first()

        assertEquals(1, result.size)
    }

    // --- calculateTripStatistics ---

    @Test
    fun `calculateTripStatistics delegates to repository`() = runTest {
        val stats = RepoTripStatistics(
            durationSeconds = 3600,
            distanceMeters = 19446.0,
            averageSpeedKnots = 5.0,
            maxSpeedKnots = 8.0
        )
        coEvery { repository.calculateTripStatistics("trip1") } returns stats

        val result = viewModel.calculateTripStatistics("trip1")

        assertEquals(5.0, result.averageSpeedKnots, 0.01)
        assertEquals(8.0, result.maxSpeedKnots, 0.01)
    }

    // --- updateTrip ---

    @Test
    fun `updateTrip calls repository`() = runTest {
        val trip = TripEntity(id = "trip1", boatId = "boat1", startTime = Date())

        viewModel.updateTrip(trip)

        coVerify { repository.updateTrip(trip) }
    }

    // --- updateTripManualData ---

    @Test
    fun `updateTripManualData calls repository and triggers sync`() = runTest {
        val trip = TripEntity(id = "trip1", boatId = "boat1", startTime = Date())

        viewModel.updateTripManualData(trip)

        coVerify { repository.updateTrip(trip) }
        verify { syncOrchestrator.triggerImmediateSync() }
    }

    @Test
    fun `updateTripManualData sets error on failure`() = runTest {
        val trip = TripEntity(id = "trip1", boatId = "boat1", startTime = Date())
        coEvery { repository.updateTrip(any()) } throws RuntimeException("DB error")

        viewModel.updateTripManualData(trip)

        assertNotNull(viewModel.errorMessage.value)
        assertTrue(viewModel.errorMessage.value!!.contains("Failed to update manual data"))
    }

    // --- deleteTrip ---

    @Test
    fun `deleteTrip calls repository`() = runTest {
        val trip = TripEntity(id = "trip1", boatId = "boat1", startTime = Date())

        viewModel.deleteTrip(trip)

        coVerify { repository.deleteTrip(trip) }
    }

    // --- clearError ---

    @Test
    fun `clearError clears errorMessage`() {
        viewModel.clearError()

        assertNull(viewModel.errorMessage.value)
    }

    // --- forceCleanup ---

    @Test
    fun `forceCleanup resets tracking state when service not bound`() = runTest {
        viewModel.forceCleanup()

        assertFalse(viewModel.isTracking.value)
        assertNull(viewModel.currentTripId.value)
        assertNull(viewModel.currentTrip.value)
    }
}
