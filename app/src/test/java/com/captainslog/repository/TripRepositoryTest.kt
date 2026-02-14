package com.captainslog.repository

import android.content.Context
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.GpsPointDao
import com.captainslog.database.dao.TripDao
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.TripEntity
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

/**
 * Unit tests for TripRepository
 */
class TripRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var tripDao: TripDao
    private lateinit var gpsPointDao: GpsPointDao
    private lateinit var context: Context
    private lateinit var connectionManager: com.captainslog.connection.ConnectionManager
    private lateinit var syncOrchestratorLazy: dagger.Lazy<com.captainslog.sync.SyncOrchestrator>
    private lateinit var repository: TripRepository

    @BeforeEach
    fun setup() {
        database = mockk()
        tripDao = mockk()
        gpsPointDao = mockk()
        context = mockk()
        connectionManager = mockk(relaxed = true)

        // Mock SyncOrchestrator and Lazy wrapper
        val syncOrchestrator = mockk<com.captainslog.sync.SyncOrchestrator>(relaxed = true)
        syncOrchestratorLazy = mockk()
        every { syncOrchestratorLazy.get() } returns syncOrchestrator

        every { database.tripDao() } returns tripDao
        every { database.gpsPointDao() } returns gpsPointDao

        repository = TripRepository(database, context, connectionManager, syncOrchestratorLazy)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `insertTrip should call tripDao insertTrip`() = runBlocking {
        val trip = TripEntity(
            id = "test-trip-1",
            boatId = "boat-1",
            startTime = Date(),
            waterType = "inland",
            role = "captain"
        )
        
        coEvery { tripDao.insertTrip(trip) } returns Unit
        
        repository.insertTrip(trip)
        
        coVerify { tripDao.insertTrip(trip) }
    }

    @Test
    fun `getTripById should return trip from database`() = runBlocking {
        val tripId = "test-trip-1"
        val expectedTrip = TripEntity(
            id = tripId,
            boatId = "boat-1",
            startTime = Date(),
            waterType = "inland",
            role = "captain"
        )
        
        coEvery { tripDao.getTripById(tripId) } returns expectedTrip
        
        val result = repository.getTripById(tripId)
        
        assertEquals(expectedTrip, result)
        coVerify { tripDao.getTripById(tripId) }
    }

    @Test
    fun `getAllTrips should return flow of trips`() = runBlocking {
        val trips = listOf(
            TripEntity(
                id = "trip-1",
                boatId = "boat-1",
                startTime = Date(),
                waterType = "inland",
                role = "captain"
            ),
            TripEntity(
                id = "trip-2",
                boatId = "boat-1",
                startTime = Date(),
                waterType = "coastal",
                role = "crew"
            )
        )
        
        coEvery { tripDao.getAllTrips() } returns flowOf(trips)
        
        val result = repository.getAllTrips()
        
        coVerify { tripDao.getAllTrips() }
    }

    @Test
    fun `calculateTripStatistics should return zero stats for empty GPS points`() = runBlocking {
        val tripId = "test-trip-1"
        
        coEvery { gpsPointDao.getGpsPointsForTripSync(tripId) } returns emptyList()
        
        val stats = repository.calculateTripStatistics(tripId)
        
        assertEquals(0L, stats.durationSeconds)
        assertEquals(0.0, stats.distanceMeters, 0.01)
        assertEquals(0.0, stats.averageSpeedKnots, 0.01)
        assertEquals(0.0, stats.maxSpeedKnots, 0.01)
    }

    @Test
    fun `calculateTripStatistics should calculate duration correctly`() = runBlocking {
        val tripId = "test-trip-1"
        val startTime = Date(1000000L)
        val endTime = Date(1060000L) // 60 seconds later
        
        val gpsPoints = listOf(
            GpsPointEntity(
                tripId = tripId,
                latitude = 0.0,
                longitude = 0.0,
                timestamp = startTime
            ),
            GpsPointEntity(
                tripId = tripId,
                latitude = 0.0,
                longitude = 0.0,
                timestamp = endTime
            )
        )
        
        coEvery { gpsPointDao.getGpsPointsForTripSync(tripId) } returns gpsPoints
        
        val stats = repository.calculateTripStatistics(tripId)
        
        assertEquals(60L, stats.durationSeconds)
    }

    @Test
    fun `calculateTripStatistics should calculate distance for two points`() = runBlocking {
        val tripId = "test-trip-1"
        
        // Two points approximately 111km apart (1 degree latitude difference)
        val gpsPoints = listOf(
            GpsPointEntity(
                tripId = tripId,
                latitude = 0.0,
                longitude = 0.0,
                timestamp = Date(1000000L)
            ),
            GpsPointEntity(
                tripId = tripId,
                latitude = 1.0,
                longitude = 0.0,
                timestamp = Date(1060000L)
            )
        )
        
        coEvery { gpsPointDao.getGpsPointsForTripSync(tripId) } returns gpsPoints
        
        val stats = repository.calculateTripStatistics(tripId)
        
        // Distance should be approximately 111,000 meters (1 degree latitude)
        assertTrue(stats.distanceMeters > 110000.0)
        assertTrue(stats.distanceMeters < 112000.0)
    }

    @Test
    fun `calculateTripStatistics should calculate average and max speed`() = runBlocking {
        val tripId = "test-trip-1"
        
        val gpsPoints = listOf(
            GpsPointEntity(
                tripId = tripId,
                latitude = 0.0,
                longitude = 0.0,
                speed = 5.0f, // 5 m/s
                timestamp = Date(1000000L)
            ),
            GpsPointEntity(
                tripId = tripId,
                latitude = 0.0,
                longitude = 0.0,
                speed = 10.0f, // 10 m/s
                timestamp = Date(1060000L)
            ),
            GpsPointEntity(
                tripId = tripId,
                latitude = 0.0,
                longitude = 0.0,
                speed = 7.5f, // 7.5 m/s
                timestamp = Date(1120000L)
            )
        )
        
        coEvery { gpsPointDao.getGpsPointsForTripSync(tripId) } returns gpsPoints
        
        val stats = repository.calculateTripStatistics(tripId)
        
        // Average speed: (5 + 10 + 7.5) / 3 = 7.5 m/s = 14.58 knots
        assertEquals(14.58, stats.averageSpeedKnots, 0.1)
        
        // Max speed: 10 m/s = 19.44 knots
        assertEquals(19.44, stats.maxSpeedKnots, 0.1)
    }

    @Test
    fun `markTripAsSynced should call tripDao markAsSynced`() = runBlocking {
        val tripId = "test-trip-1"
        
        coEvery { tripDao.markAsSynced(tripId) } returns Unit
        
        repository.markTripAsSynced(tripId)
        
        coVerify { tripDao.markAsSynced(tripId) }
    }

    @Test
    fun `getUnsyncedTrips should return unsynced trips from database`() = runBlocking {
        val unsyncedTrips = listOf(
            TripEntity(
                id = "trip-1",
                boatId = "boat-1",
                startTime = Date(),
                waterType = "inland",
                role = "captain",
                synced = false
            )
        )
        
        coEvery { tripDao.getUnsyncedTrips() } returns unsyncedTrips
        
        val result = repository.getUnsyncedTrips()
        
        assertEquals(unsyncedTrips, result)
        coVerify { tripDao.getUnsyncedTrips() }
    }
}
