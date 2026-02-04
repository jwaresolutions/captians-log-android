package com.captainslog.repository

import android.content.Context
import com.captainslog.database.AppDatabase
import com.captainslog.database.dao.BoatDao
import com.captainslog.database.entities.BoatEntity
import com.captainslog.connection.ConnectionManager
import com.captainslog.network.ApiService
import com.captainslog.network.models.ApiDataResponse
import com.captainslog.network.models.ApiListResponse
import com.captainslog.network.models.BoatResponse
import com.captainslog.network.models.CreateBoatRequest
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.util.Date

/**
 * Unit tests for BoatRepository
 */
class BoatRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var boatDao: BoatDao
    private lateinit var apiService: ApiService
    private lateinit var connectionManager: ConnectionManager
    private lateinit var context: Context
    private lateinit var repository: BoatRepository

    @BeforeEach
    fun setup() {
        database = mockk()
        boatDao = mockk()
        apiService = mockk()
        connectionManager = mockk()
        context = mockk()
        
        every { database.boatDao() } returns boatDao
        coEvery { connectionManager.getApiService() } returns apiService
        
        repository = BoatRepository(database, connectionManager, context)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllBoats should return flow of boats`() = runBlocking {
        val boats = listOf(
            BoatEntity(
                id = "boat-1",
                name = "Test Boat 1",
                enabled = true,
                isActive = true
            ),
            BoatEntity(
                id = "boat-2",
                name = "Test Boat 2",
                enabled = true,
                isActive = false
            )
        )
        
        coEvery { boatDao.getAllBoats() } returns flowOf(boats)
        
        val result = repository.getAllBoats()
        
        coVerify { boatDao.getAllBoats() }
    }

    @Test
    fun `getBoatById should return boat from database`() = runBlocking {
        val boatId = "boat-1"
        val expectedBoat = BoatEntity(
            id = boatId,
            name = "Test Boat",
            enabled = true,
            isActive = false
        )
        
        coEvery { boatDao.getBoatById(boatId) } returns expectedBoat
        
        val result = repository.getBoatById(boatId)
        
        assertEquals(expectedBoat, result)
        coVerify { boatDao.getBoatById(boatId) }
    }

    @Test
    fun `getActiveBoat should return active boat from database`() = runBlocking {
        val activeBoat = BoatEntity(
            id = "boat-1",
            name = "Active Boat",
            enabled = true,
            isActive = true
        )
        
        coEvery { boatDao.getActiveBoat() } returns activeBoat
        
        val result = repository.getActiveBoat()
        
        assertEquals(activeBoat, result)
        coVerify { boatDao.getActiveBoat() }
    }

    @Test
    fun `createBoat should insert boat locally and sync to API`() = runBlocking {
        val boatName = "New Boat"
        val apiBoat = BoatResponse(
            id = "api-boat-1",
            name = boatName,
            enabled = true,
            isActive = true,
            metadata = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
        
        coEvery { boatDao.insertBoat(any()) } returns Unit
        coEvery { apiService.createBoat(any()) } returns Response.success(ApiDataResponse(data = apiBoat))
        
        val result = repository.createBoat(boatName)
        
        assertTrue(result.isSuccess)
        coVerify { boatDao.insertBoat(any()) }
        coVerify { apiService.createBoat(CreateBoatRequest(name = boatName)) }
    }

    @Test
    fun `createBoat should succeed locally even if API fails`() = runBlocking {
        val boatName = "New Boat"
        
        coEvery { boatDao.insertBoat(any()) } returns Unit
        coEvery { apiService.createBoat(any()) } throws Exception("Network error")
        
        val result = repository.createBoat(boatName)
        
        assertTrue(result.isSuccess)
        coVerify { boatDao.insertBoat(any()) }
    }

    @Test
    fun `updateBoatStatus should update boat and sync to API`() = runBlocking {
        val boatId = "boat-1"
        val boat = BoatEntity(
            id = boatId,
            name = "Test Boat",
            enabled = true,
            isActive = false
        )
        
        coEvery { boatDao.getBoatById(boatId) } returns boat
        coEvery { boatDao.updateBoat(any()) } returns Unit
        coEvery { apiService.updateBoatStatus(boatId, any()) } returns Response.success(mockk())
        coEvery { boatDao.markAsSynced(boatId) } returns Unit
        
        val result = repository.updateBoatStatus(boatId, false)
        
        assertTrue(result.isSuccess)
        coVerify { boatDao.updateBoat(any()) }
        coVerify { apiService.updateBoatStatus(boatId, mapOf("enabled" to false)) }
    }

    @Test
    fun `setActiveBoat should clear other active boats and set new one`() = runBlocking {
        val boatId = "boat-1"
        val boat = BoatEntity(
            id = boatId,
            name = "Test Boat",
            enabled = true,
            isActive = false
        )
        
        coEvery { boatDao.clearActiveBoat() } returns Unit
        coEvery { boatDao.setActiveBoat(boatId) } returns Unit
        coEvery { boatDao.getBoatById(boatId) } returns boat
        coEvery { boatDao.updateBoat(any()) } returns Unit
        coEvery { apiService.setActiveBoat(boatId) } returns Response.success(mockk())
        coEvery { boatDao.markAsSynced(boatId) } returns Unit
        
        val result = repository.setActiveBoat(boatId)
        
        assertTrue(result.isSuccess)
        coVerify { boatDao.clearActiveBoat() }
        coVerify { boatDao.setActiveBoat(boatId) }
        coVerify { apiService.setActiveBoat(boatId) }
    }

    @Test
    fun `syncBoatsFromApi should fetch boats and insert into database`() = runBlocking {
        val apiBoats = listOf(
            BoatResponse(
                id = "boat-1",
                name = "Boat 1",
                enabled = true,
                isActive = true,
                metadata = null,
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z"
            ),
            BoatResponse(
                id = "boat-2",
                name = "Boat 2",
                enabled = true,
                isActive = false,
                metadata = null,
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z"
            )
        )
        
        val wrappedResponse = ApiListResponse(
            data = apiBoats,
            count = apiBoats.size,
            timestamp = "2024-01-01T00:00:00Z"
        )
        coEvery { apiService.getBoats() } returns Response.success(wrappedResponse)
        coEvery { boatDao.insertBoats(any()) } returns Unit
        
        val result = repository.syncBoatsFromApi()
        
        assertTrue(result.isSuccess)
        coVerify { apiService.getBoats() }
        coVerify { boatDao.insertBoats(any()) }
    }
}
