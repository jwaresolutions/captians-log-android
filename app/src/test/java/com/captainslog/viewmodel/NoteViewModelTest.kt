package com.captainslog.viewmodel

import com.captainslog.database.entities.NoteEntity
import com.captainslog.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NoteViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        // Mock init calls (syncNotesFromApi and getAllTags are called in init)
        coEvery { repository.syncNotesFromApi() } returns Result.success(Unit)
        coEvery { repository.getAllTags() } returns Result.success(listOf("tag1", "tag2"))
        viewModel = NoteViewModel(repository)
    }

    // --- getAllNotes ---

    @Test
    fun `getAllNotes returns flow from repository`() = runTest {
        val notes = listOf(
            NoteEntity(content = "Note 1", type = "general"),
            NoteEntity(content = "Note 2", type = "boat", boatId = "b1")
        )
        every { repository.getAllNotes() } returns flowOf(notes)

        val result = viewModel.getAllNotes().first()

        assertEquals(2, result.size)
    }

    // --- getNotesByType ---

    @Test
    fun `getNotesByType delegates to repository`() = runTest {
        val notes = listOf(NoteEntity(content = "Boat note", type = "boat", boatId = "b1"))
        every { repository.getNotesByType("boat") } returns flowOf(notes)

        val result = viewModel.getNotesByType("boat").first()

        assertEquals(1, result.size)
        assertEquals("boat", result[0].type)
    }

    // --- getNotesByBoat ---

    @Test
    fun `getNotesByBoat delegates to repository`() = runTest {
        every { repository.getNotesByBoat("b1") } returns flowOf(emptyList())

        val result = viewModel.getNotesByBoat("b1").first()

        assertTrue(result.isEmpty())
    }

    // --- getNotesByTrip ---

    @Test
    fun `getNotesByTrip delegates to repository`() = runTest {
        every { repository.getNotesByTrip("t1") } returns flowOf(emptyList())

        val result = viewModel.getNotesByTrip("t1").first()

        assertTrue(result.isEmpty())
    }

    // --- searchNotes ---

    @Test
    fun `searchNotes updates searchQuery and returns flow`() = runTest {
        val notes = listOf(NoteEntity(content = "found it", type = "general"))
        every { repository.searchNotes("found") } returns flowOf(notes)

        val result = viewModel.searchNotes("found").first()

        assertEquals("found", viewModel.searchQuery.value)
        assertEquals(1, result.size)
    }

    // --- getNoteById ---

    @Test
    fun `getNoteById returns note from repository`() = runTest {
        val note = NoteEntity(content = "Test", type = "general")
        coEvery { repository.getNoteById(note.id) } returns note

        val result = viewModel.getNoteById(note.id)

        assertEquals("Test", result?.content)
    }

    @Test
    fun `getNoteById returns null for unknown id`() = runTest {
        coEvery { repository.getNoteById("unknown") } returns null

        val result = viewModel.getNoteById("unknown")

        assertNull(result)
    }

    // --- createNote ---

    @Test
    fun `createNote with valid general note calls repository and sets success`() = runTest {
        coEvery { repository.createNote("Content", "general", null, null, emptyList()) } returns Result.success(
            NoteEntity(content = "Content", type = "general")
        )

        viewModel.createNote("Content", "general")

        coVerify { repository.createNote("Content", "general", null, null, emptyList()) }
        assertEquals("Note created successfully", viewModel.successMessage.value)
    }

    @Test
    fun `createNote with blank content sets error`() = runTest {
        viewModel.createNote("", "general")

        assertEquals("Note content cannot be empty", viewModel.error.value)
    }

    @Test
    fun `createNote with invalid type sets error`() = runTest {
        viewModel.createNote("Content", "invalid_type")

        assertEquals("Invalid note type", viewModel.error.value)
    }

    @Test
    fun `createNote boat type without boatId sets error`() = runTest {
        viewModel.createNote("Content", "boat")

        assertEquals("Boat ID is required for boat-specific notes", viewModel.error.value)
    }

    @Test
    fun `createNote boat type with blank boatId sets error`() = runTest {
        viewModel.createNote("Content", "boat", boatId = "")

        assertEquals("Boat ID is required for boat-specific notes", viewModel.error.value)
    }

    @Test
    fun `createNote trip type without tripId sets error`() = runTest {
        viewModel.createNote("Content", "trip")

        assertEquals("Trip ID is required for trip-specific notes", viewModel.error.value)
    }

    @Test
    fun `createNote boat type with valid boatId succeeds`() = runTest {
        coEvery { repository.createNote("Content", "boat", "b1", null, emptyList()) } returns Result.success(
            NoteEntity(content = "Content", type = "boat", boatId = "b1")
        )

        viewModel.createNote("Content", "boat", boatId = "b1")

        coVerify { repository.createNote("Content", "boat", "b1", null, emptyList()) }
        assertEquals("Note created successfully", viewModel.successMessage.value)
    }

    @Test
    fun `createNote with tags passes tags to repository`() = runTest {
        val tags = listOf("engine", "repair")
        coEvery { repository.createNote("Content", "general", null, null, tags) } returns Result.success(
            NoteEntity(content = "Content", type = "general", tags = tags)
        )

        viewModel.createNote("Content", "general", tags = tags)

        coVerify { repository.createNote("Content", "general", null, null, tags) }
    }

    @Test
    fun `createNote repository failure sets error`() = runTest {
        coEvery { repository.createNote(any(), any(), any(), any(), any()) } returns Result.failure(Exception("DB error"))

        viewModel.createNote("Content", "general")

        assertNotNull(viewModel.error.value)
    }

    // --- updateNote ---

    @Test
    fun `updateNote with valid content calls repository`() = runTest {
        coEvery { repository.updateNote("n1", "Updated", null) } returns Result.success(
            NoteEntity(content = "Updated", type = "general")
        )

        viewModel.updateNote("n1", content = "Updated")

        coVerify { repository.updateNote("n1", "Updated", null) }
        assertEquals("Note updated successfully", viewModel.successMessage.value)
    }

    @Test
    fun `updateNote with blank content sets error`() = runTest {
        viewModel.updateNote("n1", content = "")

        assertEquals("Note content cannot be empty", viewModel.error.value)
    }

    @Test
    fun `updateNote with null content is allowed`() = runTest {
        coEvery { repository.updateNote("n1", null, listOf("tag")) } returns Result.success(
            NoteEntity(content = "Original", type = "general")
        )

        viewModel.updateNote("n1", tags = listOf("tag"))

        coVerify { repository.updateNote("n1", null, listOf("tag")) }
    }

    @Test
    fun `updateNote failure sets error`() = runTest {
        coEvery { repository.updateNote(any(), any(), any()) } returns Result.failure(Exception("Failed"))

        viewModel.updateNote("n1", content = "Updated")

        assertNotNull(viewModel.error.value)
    }

    // --- deleteNote ---

    @Test
    fun `deleteNote calls repository and sets success`() = runTest {
        coEvery { repository.deleteNote("n1") } returns Result.success(Unit)

        viewModel.deleteNote("n1")

        coVerify { repository.deleteNote("n1") }
        assertEquals("Note deleted successfully", viewModel.successMessage.value)
    }

    @Test
    fun `deleteNote failure sets error`() = runTest {
        coEvery { repository.deleteNote("n1") } returns Result.failure(Exception("Not found"))

        viewModel.deleteNote("n1")

        assertNotNull(viewModel.error.value)
    }

    // --- setSelectedNoteType ---

    @Test
    fun `setSelectedNoteType updates state`() {
        viewModel.setSelectedNoteType("boat")

        assertEquals("boat", viewModel.selectedNoteType.value)
    }

    @Test
    fun `selectedNoteType defaults to general`() {
        assertEquals("general", viewModel.selectedNoteType.value)
    }

    // --- loadAvailableTags ---

    @Test
    fun `loadAvailableTags populates tags from repository`() = runTest {
        coEvery { repository.getAllTags() } returns Result.success(listOf("engine", "hull"))

        viewModel.loadAvailableTags()

        assertEquals(listOf("engine", "hull"), viewModel.availableTags.value)
    }

    @Test
    fun `loadAvailableTags with failure keeps existing tags`() = runTest {
        // First load succeeds (from init)
        coEvery { repository.getAllTags() } returns Result.success(listOf("tag1", "tag2"))
        viewModel.loadAvailableTags()
        assertEquals(listOf("tag1", "tag2"), viewModel.availableTags.value)

        // Second load fails - tags should remain
        coEvery { repository.getAllTags() } returns Result.failure(Exception("Failed"))
        viewModel.loadAvailableTags()

        assertEquals(listOf("tag1", "tag2"), viewModel.availableTags.value)
    }

    // --- sync ---

    @Test
    fun `syncNotesFromApi calls repository`() = runTest {
        coEvery { repository.syncNotesFromApi() } returns Result.success(Unit)

        viewModel.syncNotesFromApi()

        coVerify(atLeast = 1) { repository.syncNotesFromApi() }
    }

    @Test
    fun `syncNotesToApi calls repository`() = runTest {
        coEvery { repository.syncNotesToApi() } returns Result.success(Unit)

        viewModel.syncNotesToApi()

        coVerify { repository.syncNotesToApi() }
    }

    // --- clearSuccessMessage ---

    @Test
    fun `clearSuccessMessage clears success state`() = runTest {
        coEvery { repository.deleteNote("n1") } returns Result.success(Unit)
        viewModel.deleteNote("n1")
        assertNotNull(viewModel.successMessage.value)

        viewModel.clearSuccessMessage()

        assertNull(viewModel.successMessage.value)
    }
}
