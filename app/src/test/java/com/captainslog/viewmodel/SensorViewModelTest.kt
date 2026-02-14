package com.captainslog.viewmodel

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.captainslog.network.models.SensorTypeResponse
import com.captainslog.repository.SensorRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.util.Date
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SensorViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var application: Application
    private lateinit var sensorRepository: SensorRepository
    private lateinit var viewModel: SensorViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        sensorRepository = mockk(relaxed = true)

        // BluetoothManager is created inside SensorViewModel constructor
        val btManager = mockk<BluetoothManager>(relaxed = true)
        every { btManager.adapter } returns mockk<BluetoothAdapter>(relaxed = true)
        every { application.getSystemService(Context.BLUETOOTH_SERVICE) } returns btManager

        coEvery { sensorRepository.getSensorTypes() } returns Result.success(emptyList())
        viewModel = SensorViewModel(application, sensorRepository)
    }

    // --- initial state ---

    @Test
    fun `initial uiState is not discovering`() {
        assertFalse(viewModel.uiState.value.isDiscovering)
    }

    @Test
    fun `initial uiState is not connecting`() {
        assertFalse(viewModel.uiState.value.isConnecting)
    }

    @Test
    fun `initial uiState has no connected device`() {
        assertNull(viewModel.uiState.value.connectedDevice)
    }

    @Test
    fun `initial uiState has no connection error`() {
        assertNull(viewModel.uiState.value.connectionError)
    }

    @Test
    fun `initial currentTripId is null`() {
        assertNull(viewModel.currentTripId.value)
    }

    // --- startDeviceDiscovery ---

    @Test
    fun `startDeviceDiscovery sets isDiscovering true`() {
        viewModel.startDeviceDiscovery()

        assertTrue(viewModel.uiState.value.isDiscovering)
    }

    // --- stopDeviceDiscovery ---

    @Test
    fun `stopDeviceDiscovery sets isDiscovering false`() {
        viewModel.startDeviceDiscovery()
        viewModel.stopDeviceDiscovery()

        assertFalse(viewModel.uiState.value.isDiscovering)
    }

    // --- disconnect ---

    @Test
    fun `disconnect clears connected device and error`() {
        viewModel.disconnect()

        assertNull(viewModel.uiState.value.connectedDevice)
        assertNull(viewModel.uiState.value.connectionError)
    }

    // --- setCurrentTripId ---

    @Test
    fun `setCurrentTripId updates state`() {
        viewModel.setCurrentTripId("trip123")

        assertEquals("trip123", viewModel.currentTripId.value)
    }

    @Test
    fun `setCurrentTripId with null clears trip`() {
        viewModel.setCurrentTripId("trip123")
        viewModel.setCurrentTripId(null)

        assertNull(viewModel.currentTripId.value)
    }

    // --- loadSensorTypes ---

    @Test
    fun `loadSensorTypes success updates sensor types`() = runTest {
        val types = listOf(
            SensorTypeResponse(id = "1", name = "Temperature", unit = "°C", loggingFrequency = "1s", description = null, createdAt = Date(), updatedAt = Date())
        )
        coEvery { sensorRepository.getSensorTypes() } returns Result.success(types)

        viewModel.loadSensorTypes()

        assertEquals(1, viewModel.sensorTypes.value.size)
        assertEquals("Temperature", viewModel.sensorTypes.value[0].name)
        assertFalse(viewModel.uiState.value.isLoadingSensorTypes)
    }

    @Test
    fun `loadSensorTypes failure sets error`() = runTest {
        coEvery { sensorRepository.getSensorTypes() } returns Result.failure(Exception("Network error"))

        viewModel.loadSensorTypes()

        assertNotNull(viewModel.uiState.value.sensorTypesError)
        assertFalse(viewModel.uiState.value.isLoadingSensorTypes)
    }

    // --- createSensorType ---

    @Test
    fun `createSensorType success reloads types`() = runTest {
        val newType = SensorTypeResponse(id = "2", name = "Wind", unit = "knots", loggingFrequency = "5s", description = null, createdAt = Date(), updatedAt = Date())
        coEvery { sensorRepository.createSensorType("Wind", "knots", "5s", null) } returns Result.success(newType)
        coEvery { sensorRepository.getSensorTypes() } returns Result.success(listOf(newType))

        viewModel.createSensorType("Wind", "knots", "5s")

        assertFalse(viewModel.uiState.value.isCreatingSensorType)
        assertNull(viewModel.uiState.value.sensorTypeCreationError)
    }

    @Test
    fun `createSensorType failure sets creation error`() = runTest {
        coEvery { sensorRepository.createSensorType(any(), any(), any(), any()) } returns
            Result.failure(Exception("Duplicate"))

        viewModel.createSensorType("Wind", "knots", "5s")

        assertNotNull(viewModel.uiState.value.sensorTypeCreationError)
        assertFalse(viewModel.uiState.value.isCreatingSensorType)
    }

    // --- clear error methods ---

    @Test
    fun `clearConnectionError clears connection error`() {
        viewModel.clearConnectionError()
        assertNull(viewModel.uiState.value.connectionError)
    }

    @Test
    fun `clearSensorTypesError clears sensor types error`() {
        viewModel.clearSensorTypesError()
        assertNull(viewModel.uiState.value.sensorTypesError)
    }

    @Test
    fun `clearSensorTypeCreationError clears creation error`() {
        viewModel.clearSensorTypeCreationError()
        assertNull(viewModel.uiState.value.sensorTypeCreationError)
    }
}
