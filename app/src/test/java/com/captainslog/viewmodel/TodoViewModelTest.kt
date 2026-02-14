package com.captainslog.viewmodel

import com.captainslog.database.entities.TodoItemEntity
import com.captainslog.database.entities.TodoListEntity
import com.captainslog.repository.TodoRepository
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
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var todoRepository: TodoRepository
    private lateinit var viewModel: TodoViewModel

    @Before
    fun setup() {
        todoRepository = mockk(relaxed = true)
        viewModel = TodoViewModel(todoRepository)
    }

    // --- selectTodoList ---

    @Test
    fun `selectTodoList updates selectedListId`() {
        viewModel.selectTodoList("list1")

        assertEquals("list1", viewModel.selectedListId.value)
    }

    @Test
    fun `selectTodoList with null clears selection`() {
        viewModel.selectTodoList("list1")
        viewModel.selectTodoList(null)

        assertNull(viewModel.selectedListId.value)
    }

    // --- createTodoList ---

    @Test
    fun `createTodoList success clears loading`() = runTest {
        val list = TodoListEntity(id = "l1", title = "My List", createdAt = Date(), updatedAt = Date())
        coEvery { todoRepository.createTodoList("My List", null) } returns Result.success(list)

        viewModel.createTodoList("My List")

        coVerify { todoRepository.createTodoList("My List", null) }
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `createTodoList with boatId passes boatId`() = runTest {
        val list = TodoListEntity(id = "l1", title = "Boat List", boatId = "b1", createdAt = Date(), updatedAt = Date())
        coEvery { todoRepository.createTodoList("Boat List", "b1") } returns Result.success(list)

        viewModel.createTodoList("Boat List", "b1")

        coVerify { todoRepository.createTodoList("Boat List", "b1") }
    }

    @Test
    fun `createTodoList failure sets error in uiState`() = runTest {
        coEvery { todoRepository.createTodoList("Fail", null) } returns Result.failure(Exception("DB error"))

        viewModel.createTodoList("Fail")

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("DB error", viewModel.uiState.value.error)
    }

    @Test
    fun `createTodoList failure without message uses default`() = runTest {
        coEvery { todoRepository.createTodoList("Fail", null) } returns Result.failure(Exception())

        viewModel.createTodoList("Fail")

        assertEquals("Failed to create todo list", viewModel.uiState.value.error)
    }

    // --- updateTodoList ---

    @Test
    fun `updateTodoList success clears loading`() = runTest {
        val list = TodoListEntity(id = "l1", title = "Updated", createdAt = Date(), updatedAt = Date())
        coEvery { todoRepository.updateTodoList("l1", "Updated", null) } returns Result.success(list)

        viewModel.updateTodoList("l1", "Updated", null)

        coVerify { todoRepository.updateTodoList("l1", "Updated", null) }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `updateTodoList failure sets error`() = runTest {
        coEvery { todoRepository.updateTodoList("l1", "X", null) } returns Result.failure(Exception("Not found"))

        viewModel.updateTodoList("l1", "X", null)

        assertEquals("Not found", viewModel.uiState.value.error)
    }

    // --- deleteTodoList ---

    @Test
    fun `deleteTodoList success clears loading`() = runTest {
        coEvery { todoRepository.deleteTodoList("l1") } returns Result.success(Unit)

        viewModel.deleteTodoList("l1")

        coVerify { todoRepository.deleteTodoList("l1") }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `deleteTodoList clears selection if deleted list was selected`() = runTest {
        viewModel.selectTodoList("l1")
        coEvery { todoRepository.deleteTodoList("l1") } returns Result.success(Unit)

        viewModel.deleteTodoList("l1")

        assertNull(viewModel.selectedListId.value)
    }

    @Test
    fun `deleteTodoList does not clear selection if different list deleted`() = runTest {
        viewModel.selectTodoList("l2")
        coEvery { todoRepository.deleteTodoList("l1") } returns Result.success(Unit)

        viewModel.deleteTodoList("l1")

        assertEquals("l2", viewModel.selectedListId.value)
    }

    @Test
    fun `deleteTodoList failure sets error`() = runTest {
        coEvery { todoRepository.deleteTodoList("l1") } returns Result.failure(Exception("Constraint"))

        viewModel.deleteTodoList("l1")

        assertEquals("Constraint", viewModel.uiState.value.error)
    }

    // --- createTodoItem ---

    @Test
    fun `createTodoItem success clears loading`() = runTest {
        val item = TodoItemEntity(id = "i1", todoListId = "l1", content = "Buy rope", createdAt = Date(), updatedAt = Date())
        coEvery { todoRepository.createTodoItem("l1", "Buy rope") } returns Result.success(item)

        viewModel.createTodoItem("l1", "Buy rope")

        coVerify { todoRepository.createTodoItem("l1", "Buy rope") }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `createTodoItem failure sets error`() = runTest {
        coEvery { todoRepository.createTodoItem("l1", "Item") } returns Result.failure(Exception("Failed"))

        viewModel.createTodoItem("l1", "Item")

        assertEquals("Failed", viewModel.uiState.value.error)
    }

    // --- updateTodoItem ---

    @Test
    fun `updateTodoItem success clears loading`() = runTest {
        val item = TodoItemEntity(id = "i1", todoListId = "l1", content = "Updated", createdAt = Date(), updatedAt = Date())
        coEvery { todoRepository.updateTodoItem("i1", "Updated", null) } returns Result.success(item)

        viewModel.updateTodoItem("i1", "Updated", null)

        coVerify { todoRepository.updateTodoItem("i1", "Updated", null) }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `updateTodoItem with completion flag`() = runTest {
        val item = TodoItemEntity(id = "i1", todoListId = "l1", content = "Item", completed = true, createdAt = Date(), updatedAt = Date())
        coEvery { todoRepository.updateTodoItem("i1", null, true) } returns Result.success(item)

        viewModel.updateTodoItem("i1", null, true)

        coVerify { todoRepository.updateTodoItem("i1", null, true) }
    }

    @Test
    fun `updateTodoItem failure sets error`() = runTest {
        coEvery { todoRepository.updateTodoItem("i1", "X", null) } returns Result.failure(Exception())

        viewModel.updateTodoItem("i1", "X", null)

        assertEquals("Failed to update todo item", viewModel.uiState.value.error)
    }

    // --- toggleTodoItemCompletion ---

    @Test
    fun `toggleTodoItemCompletion calls repository`() = runTest {
        val item = TodoItemEntity(id = "i1", todoListId = "l1", content = "Item", completed = true, createdAt = Date(), updatedAt = Date())
        coEvery { todoRepository.toggleTodoItemCompletion("i1") } returns Result.success(item)

        viewModel.toggleTodoItemCompletion("i1")

        coVerify { todoRepository.toggleTodoItemCompletion("i1") }
    }

    @Test
    fun `toggleTodoItemCompletion failure sets error`() = runTest {
        coEvery { todoRepository.toggleTodoItemCompletion("i1") } returns Result.failure(Exception("Toggle failed"))

        viewModel.toggleTodoItemCompletion("i1")

        assertEquals("Toggle failed", viewModel.uiState.value.error)
    }

    // --- deleteTodoItem ---

    @Test
    fun `deleteTodoItem success clears loading`() = runTest {
        coEvery { todoRepository.deleteTodoItem("i1") } returns Result.success(Unit)

        viewModel.deleteTodoItem("i1")

        coVerify { todoRepository.deleteTodoItem("i1") }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `deleteTodoItem failure sets error`() = runTest {
        coEvery { todoRepository.deleteTodoItem("i1") } returns Result.failure(Exception())

        viewModel.deleteTodoItem("i1")

        assertEquals("Failed to delete todo item", viewModel.uiState.value.error)
    }

    // --- syncTodoLists ---

    @Test
    fun `syncTodoLists success clears loading`() = runTest {
        coEvery { todoRepository.syncTodoLists() } returns Result.success(Unit)

        viewModel.syncTodoLists()

        coVerify { todoRepository.syncTodoLists() }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `syncTodoLists failure sets error`() = runTest {
        coEvery { todoRepository.syncTodoLists() } returns Result.failure(Exception("Network error"))

        viewModel.syncTodoLists()

        assertEquals("Network error", viewModel.uiState.value.error)
    }

    // --- clearError ---

    @Test
    fun `clearError clears error in uiState`() = runTest {
        coEvery { todoRepository.createTodoList("Fail", null) } returns Result.failure(Exception("Error"))
        viewModel.createTodoList("Fail")
        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }

    // --- initial uiState ---

    @Test
    fun `initial uiState has no loading and no error`() {
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    // --- Flow properties ---

    @Test
    fun `allTodoLists delegates to repository`() = runTest {
        val lists = listOf(
            TodoListEntity(id = "l1", title = "List 1", createdAt = Date(), updatedAt = Date())
        )
        every { todoRepository.getAllTodoLists() } returns flowOf(lists)

        // Re-create to pick up the mock
        viewModel = TodoViewModel(todoRepository)
        val result = viewModel.allTodoLists.first()

        assertEquals(1, result.size)
    }
}
