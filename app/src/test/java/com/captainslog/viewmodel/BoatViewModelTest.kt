package com.captainslog.viewmodel

import com.captainslog.database.entities.BoatEntity
import com.captainslog.repository.BoatRepository
import com.captainslog.sync.SyncOrchestrator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class BoatViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var repository: BoatRepository
    private lateinit var syncOrchestrator: SyncOrchestrator
    private lateinit var viewModel: BoatViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        syncOrchestrator = mockk(relaxed = true)
        viewModel = BoatViewModel(repository, syncOrchestrator)
    }

    // --- getAllBoats ---

    @Test
    fun `getAllBoats returns flow from repository`() = runTest {
        val boats = listOf(
            BoatEntity(name = "Boat A"),
            BoatEntity(name = "Boat B")
        )
        every { repository.getAllBoats() } returns flowOf(boats)

        val result = viewModel.getAllBoats().first()

        assertEquals(2, result.size)
        assertEquals("Boat A", result[0].name)
    }

    @Test
    fun `getAllBoats returns empty list when no boats`() = runTest {
        every { repository.getAllBoats() } returns flowOf(emptyList())

        val result = viewModel.getAllBoats().first()

        assertTrue(result.isEmpty())
    }

    // --- getActiveBoat ---

    @Test
    fun `getActiveBoat returns boat from repository`() = runTest {
        val boat = BoatEntity(name = "Active Boat", isActive = true)
        coEvery { repository.getActiveBoat() } returns boat

        val result = viewModel.getActiveBoat()

        assertNotNull(result)
        assertEquals("Active Boat", result?.name)
    }

    @Test
    fun `getActiveBoat returns null when no active boat`() = runTest {
        coEvery { repository.getActiveBoat() } returns null

        val result = viewModel.getActiveBoat()

        assertNull(result)
    }

    // --- createBoat ---

    @Test
    fun `createBoat with valid name calls repository and sets success`() = runTest {
        val boat = BoatEntity(name = "New Boat")
        coEvery { repository.createBoat("New Boat") } returns Result.success(boat)

        viewModel.createBoat("New Boat")

        coVerify { repository.createBoat("New Boat") }
        assertEquals("Boat created successfully", viewModel.successMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `createBoat with blank name sets error without calling repository`() = runTest {
        viewModel.createBoat("")

        coVerify(exactly = 0) { repository.createBoat(any()) }
        assertEquals("Boat name cannot be empty", viewModel.error.value)
    }

    @Test
    fun `createBoat with whitespace-only name sets error`() = runTest {
        viewModel.createBoat("   ")

        coVerify(exactly = 0) { repository.createBoat(any()) }
        assertEquals("Boat name cannot be empty", viewModel.error.value)
    }

    @Test
    fun `createBoat when repository returns failure sets error`() = runTest {
        coEvery { repository.createBoat("Fail Boat") } returns Result.failure(Exception("Duplicate name"))

        viewModel.createBoat("Fail Boat")

        assertNotNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    // --- toggleBoatStatus ---

    @Test
    fun `toggleBoatStatus enable sets success message`() = runTest {
        coEvery { repository.updateBoatStatus("id1", true) } returns Result.success(Unit)

        viewModel.toggleBoatStatus("id1", true)

        coVerify { repository.updateBoatStatus("id1", true) }
        assertEquals("Boat enabled", viewModel.successMessage.value)
    }

    @Test
    fun `toggleBoatStatus disable sets success message`() = runTest {
        coEvery { repository.updateBoatStatus("id1", false) } returns Result.success(Unit)

        viewModel.toggleBoatStatus("id1", false)

        assertEquals("Boat disabled", viewModel.successMessage.value)
    }

    @Test
    fun `toggleBoatStatus failure sets error`() = runTest {
        coEvery { repository.updateBoatStatus("id1", true) } returns Result.failure(Exception("Not found"))

        viewModel.toggleBoatStatus("id1", true)

        assertNotNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    // --- setActiveBoat ---

    @Test
    fun `setActiveBoat success sets success message`() = runTest {
        coEvery { repository.setActiveBoat("id1") } returns Result.success(Unit)

        viewModel.setActiveBoat("id1")

        coVerify { repository.setActiveBoat("id1") }
        assertEquals("Active boat updated", viewModel.successMessage.value)
    }

    @Test
    fun `setActiveBoat failure sets error`() = runTest {
        coEvery { repository.setActiveBoat("id1") } returns Result.failure(Exception("Failed"))

        viewModel.setActiveBoat("id1")

        assertNotNull(viewModel.error.value)
    }

    // --- syncBoatsFromApi ---

    @Test
    fun `syncBoatsFromApi calls repository`() = runTest {
        coEvery { repository.syncBoatsFromApi() } returns Result.success(Unit)

        viewModel.syncBoatsFromApi()

        coVerify { repository.syncBoatsFromApi() }
        assertFalse(viewModel.isLoading.value)
    }

    // --- syncBoatsToApi ---

    @Test
    fun `syncBoatsToApi calls repository`() = runTest {
        coEvery { repository.syncBoatsToApi() } returns Result.success(Unit)

        viewModel.syncBoatsToApi()

        coVerify { repository.syncBoatsToApi() }
    }

    // --- performFullSync ---

    @Test
    fun `performFullSync calls syncOrchestrator and sets success`() = runTest {
        viewModel.performFullSync()

        verify { syncOrchestrator.syncAll() }
        assertEquals("Comprehensive sync started", viewModel.successMessage.value)
    }

    // --- clearSuccessMessage ---

    @Test
    fun `clearSuccessMessage clears the success state`() = runTest {
        coEvery { repository.createBoat("Test") } returns Result.success(BoatEntity(name = "Test"))
        viewModel.createBoat("Test")
        assertNotNull(viewModel.successMessage.value)

        viewModel.clearSuccessMessage()

        assertNull(viewModel.successMessage.value)
    }

    // --- clearError ---

    @Test
    fun `clearError clears the error state`() = runTest {
        viewModel.createBoat("")
        assertNotNull(viewModel.error.value)

        viewModel.clearError()

        assertNull(viewModel.error.value)
    }

    // --- loading state ---

    @Test
    fun `isLoading is false after operation completes`() = runTest {
        coEvery { repository.createBoat("Test") } returns Result.success(BoatEntity(name = "Test"))

        viewModel.createBoat("Test")

        assertFalse(viewModel.isLoading.value)
    }
}
