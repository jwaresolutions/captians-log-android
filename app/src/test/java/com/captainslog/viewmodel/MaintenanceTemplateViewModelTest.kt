package com.captainslog.viewmodel

import com.captainslog.database.dao.MaintenanceEventDao
import com.captainslog.database.dao.MaintenanceTemplateDao
import com.captainslog.database.entities.MaintenanceEventEntity
import com.captainslog.database.entities.MaintenanceTemplateEntity
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
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class MaintenanceTemplateViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var templateDao: MaintenanceTemplateDao
    private lateinit var eventDao: MaintenanceEventDao
    private lateinit var viewModel: MaintenanceTemplateViewModel

    @Before
    fun setup() {
        templateDao = mockk(relaxed = true)
        eventDao = mockk(relaxed = true)
        viewModel = MaintenanceTemplateViewModel(templateDao, eventDao)
    }

    // --- createTemplate ---

    @Test
    fun `createTemplate inserts template and generates event`() = runTest {
        coEvery { templateDao.insertTemplate(any()) } returns Unit
        coEvery { eventDao.insertEvent(any()) } returns Unit

        viewModel.createTemplate(
            boatId = "b1",
            title = "Oil Change",
            description = "Change engine oil",
            component = "Engine",
            estimatedCost = 50.0,
            estimatedTime = 60,
            recurrenceType = "months",
            recurrenceInterval = 3
        )

        coVerify { templateDao.insertTemplate(match { it.title == "Oil Change" && it.boatId == "b1" }) }
        coVerify { eventDao.insertEvent(any()) }
        assertEquals("Template created successfully", viewModel.successMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `createTemplate with null optional fields`() = runTest {
        coEvery { templateDao.insertTemplate(any()) } returns Unit
        coEvery { eventDao.insertEvent(any()) } returns Unit

        viewModel.createTemplate(
            boatId = "b1",
            title = "Inspect Hull",
            description = "Visual inspection",
            component = "Hull",
            estimatedCost = null,
            estimatedTime = null,
            recurrenceType = "years",
            recurrenceInterval = 1
        )

        coVerify { templateDao.insertTemplate(match { it.estimatedCost == null && it.estimatedTime == null }) }
    }

    @Test
    fun `createTemplate failure sets error`() = runTest {
        coEvery { templateDao.insertTemplate(any()) } throws RuntimeException("DB full")

        viewModel.createTemplate("b1", "T", "D", "C", null, null, "days", 1)

        assertNotNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    // --- updateTemplate ---

    @Test
    fun `updateTemplate calls dao with updated timestamp`() = runTest {
        val template = MaintenanceTemplateEntity(
            boatId = "b1", title = "Oil Change", description = "Desc",
            component = "Engine", recurrenceType = "months", recurrenceInterval = 3
        )
        coEvery { templateDao.updateTemplate(any()) } returns Unit

        viewModel.updateTemplate(template)

        coVerify { templateDao.updateTemplate(match { it.title == "Oil Change" }) }
        assertEquals("Template updated successfully", viewModel.successMessage.value)
    }

    @Test
    fun `updateTemplate failure sets error`() = runTest {
        val template = MaintenanceTemplateEntity(
            boatId = "b1", title = "T", description = "D",
            component = "C", recurrenceType = "days", recurrenceInterval = 1
        )
        coEvery { templateDao.updateTemplate(any()) } throws RuntimeException("Failed")

        viewModel.updateTemplate(template)

        assertNotNull(viewModel.error.value)
    }

    // --- deleteTemplate ---

    @Test
    fun `deleteTemplate calls dao and sets success`() = runTest {
        coEvery { templateDao.deleteTemplateById("t1") } returns Unit

        viewModel.deleteTemplate("t1")

        coVerify { templateDao.deleteTemplateById("t1") }
        assertEquals("Template deleted successfully", viewModel.successMessage.value)
    }

    @Test
    fun `deleteTemplate failure sets error`() = runTest {
        coEvery { templateDao.deleteTemplateById("t1") } throws RuntimeException("Not found")

        viewModel.deleteTemplate("t1")

        assertNotNull(viewModel.error.value)
    }

    // --- completeEvent ---

    @Test
    fun `completeEvent marks event and generates next if template active`() = runTest {
        val template = MaintenanceTemplateEntity(
            boatId = "b1", title = "T", description = "D",
            component = "C", recurrenceType = "months", recurrenceInterval = 3, isActive = true
        )
        val event = MaintenanceEventEntity(templateId = template.id, dueDate = Date())

        coEvery { eventDao.completeEvent(event.id, any(), 45.0, 30, "All good") } returns Unit
        coEvery { eventDao.getEventByIdSync(event.id) } returns event
        coEvery { templateDao.getTemplateByIdSync(template.id) } returns template
        coEvery { eventDao.insertEvent(any()) } returns Unit

        viewModel.completeEvent(event.id, 45.0, 30, "All good")

        coVerify { eventDao.completeEvent(event.id, any(), 45.0, 30, "All good") }
        coVerify { eventDao.insertEvent(any()) } // Next event generated
        assertEquals("Event completed successfully", viewModel.successMessage.value)
    }

    @Test
    fun `completeEvent does not generate next if template inactive`() = runTest {
        val template = MaintenanceTemplateEntity(
            boatId = "b1", title = "T", description = "D",
            component = "C", recurrenceType = "months", recurrenceInterval = 3, isActive = false
        )
        val event = MaintenanceEventEntity(templateId = template.id, dueDate = Date())

        coEvery { eventDao.completeEvent(event.id, any(), null, null, null) } returns Unit
        coEvery { eventDao.getEventByIdSync(event.id) } returns event
        coEvery { templateDao.getTemplateByIdSync(template.id) } returns template

        viewModel.completeEvent(event.id, null, null, null)

        coVerify(exactly = 0) { eventDao.insertEvent(any()) }
    }

    @Test
    fun `completeEvent failure sets error`() = runTest {
        coEvery { eventDao.completeEvent(any(), any(), any(), any(), any()) } throws RuntimeException("Failed")

        viewModel.completeEvent("e1", null, null, null)

        assertNotNull(viewModel.error.value)
    }

    // --- formatRecurrence ---

    @Test
    fun `formatRecurrence daily singular`() {
        val template = makeTemplate(recurrenceType = "days", recurrenceInterval = 1)
        assertEquals("Daily", viewModel.formatRecurrence(template))
    }

    @Test
    fun `formatRecurrence days plural`() {
        val template = makeTemplate(recurrenceType = "days", recurrenceInterval = 5)
        assertEquals("Every 5 days", viewModel.formatRecurrence(template))
    }

    @Test
    fun `formatRecurrence weekly singular`() {
        val template = makeTemplate(recurrenceType = "weeks", recurrenceInterval = 1)
        assertEquals("Weekly", viewModel.formatRecurrence(template))
    }

    @Test
    fun `formatRecurrence months plural`() {
        val template = makeTemplate(recurrenceType = "months", recurrenceInterval = 3)
        assertEquals("Every 3 months", viewModel.formatRecurrence(template))
    }

    @Test
    fun `formatRecurrence monthly singular`() {
        val template = makeTemplate(recurrenceType = "months", recurrenceInterval = 1)
        assertEquals("Monthly", viewModel.formatRecurrence(template))
    }

    @Test
    fun `formatRecurrence yearly singular`() {
        val template = makeTemplate(recurrenceType = "years", recurrenceInterval = 1)
        assertEquals("Yearly", viewModel.formatRecurrence(template))
    }

    @Test
    fun `formatRecurrence engine hours`() {
        val template = makeTemplate(recurrenceType = "engine_hours", recurrenceInterval = 100)
        assertEquals("Every 100 engine hours", viewModel.formatRecurrence(template))
    }

    @Test
    fun `formatRecurrence unknown type`() {
        val template = makeTemplate(recurrenceType = "custom", recurrenceInterval = 2)
        assertEquals("Every 2 custom", viewModel.formatRecurrence(template))
    }

    // --- getDaysUntilDue ---

    @Test
    fun `getDaysUntilDue future event returns positive`() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 10)
        val event = MaintenanceEventEntity(templateId = "t1", dueDate = cal.time)

        val days = viewModel.getDaysUntilDue(event)

        assertTrue(days >= 9) // Allow for time-of-day rounding
    }

    @Test
    fun `getDaysUntilDue past event returns negative`() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -5)
        val event = MaintenanceEventEntity(templateId = "t1", dueDate = cal.time)

        val days = viewModel.getDaysUntilDue(event)

        assertTrue(days <= -4)
    }

    // --- getEventColor ---

    @Test
    fun `getEventColor completed returns GREEN`() {
        val event = MaintenanceEventEntity(templateId = "t1", dueDate = Date(), completedAt = Date())
        assertEquals(TaskColor.GREEN, viewModel.getEventColor(event))
    }

    @Test
    fun `getEventColor overdue returns RED`() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -2)
        val event = MaintenanceEventEntity(templateId = "t1", dueDate = cal.time)
        assertEquals(TaskColor.RED, viewModel.getEventColor(event))
    }

    @Test
    fun `getEventColor due within 7 days returns YELLOW`() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 3)
        val event = MaintenanceEventEntity(templateId = "t1", dueDate = cal.time)
        assertEquals(TaskColor.YELLOW, viewModel.getEventColor(event))
    }

    @Test
    fun `getEventColor far future returns GRAY`() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 30)
        val event = MaintenanceEventEntity(templateId = "t1", dueDate = cal.time)
        assertEquals(TaskColor.GRAY, viewModel.getEventColor(event))
    }

    // --- Flow properties ---

    @Test
    fun `getTemplateById delegates to dao`() = runTest {
        val template = makeTemplate()
        every { templateDao.getTemplateById("t1") } returns flowOf(template)

        val result = viewModel.getTemplateById("t1").first()

        assertEquals(template.title, result?.title)
    }

    @Test
    fun `getEventById delegates to dao`() = runTest {
        val event = MaintenanceEventEntity(templateId = "t1", dueDate = Date())
        every { eventDao.getEventById("e1") } returns flowOf(event)

        val result = viewModel.getEventById("e1").first()

        assertNotNull(result)
    }

    @Test
    fun `getEventsByTemplate delegates to dao`() = runTest {
        every { eventDao.getEventsByTemplate("t1") } returns flowOf(emptyList())

        val result = viewModel.getEventsByTemplate("t1").first()

        assertTrue(result.isEmpty())
    }

    // --- clearMessage ---

    @Test
    fun `clearMessage clears success`() = runTest {
        coEvery { templateDao.deleteTemplateById("t1") } returns Unit
        viewModel.deleteTemplate("t1")
        assertNotNull(viewModel.successMessage.value)

        viewModel.clearMessage()

        assertNull(viewModel.successMessage.value)
    }

    // --- helper ---

    private fun makeTemplate(
        recurrenceType: String = "months",
        recurrenceInterval: Int = 1
    ) = MaintenanceTemplateEntity(
        boatId = "b1",
        title = "Test Template",
        description = "Desc",
        component = "Engine",
        recurrenceType = recurrenceType,
        recurrenceInterval = recurrenceInterval
    )
}
