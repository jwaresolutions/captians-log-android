package com.captainslog.viewmodel

import android.app.Application
import android.net.Uri
import com.captainslog.database.entities.PhotoEntity
import com.captainslog.repository.PhotoRepository
import com.captainslog.sync.SyncOrchestrator
import com.captainslog.util.PhotoCaptureHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.Runs
import io.mockk.unmockkConstructor
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoViewModelTest {

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule()

    private lateinit var application: Application
    private lateinit var photoRepository: PhotoRepository
    private lateinit var syncOrchestrator: SyncOrchestrator
    private lateinit var viewModel: PhotoViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        photoRepository = mockk(relaxed = true)
        syncOrchestrator = mockk(relaxed = true)

        // Mock PhotoCaptureHelper constructor since it's created directly in ViewModel
        mockkConstructor(PhotoCaptureHelper::class)
        every { anyConstructed<PhotoCaptureHelper>().cleanupTempFiles() } returns Unit
        every { anyConstructed<PhotoCaptureHelper>().getMimeTypeFromUri(any()) } returns "image/jpeg"
        every { anyConstructed<PhotoCaptureHelper>().hasCameraPermission() } returns true
        every { anyConstructed<PhotoCaptureHelper>().hasStoragePermission() } returns true
        every { anyConstructed<PhotoCaptureHelper>().getRequiredPermissions() } returns arrayOf()
        every { anyConstructed<PhotoCaptureHelper>().createTempImageUri() } returns mockk()

        // Mock the init block's calls
        coEvery { photoRepository.getUnuploadedPhotoCount() } returns 0

        viewModel = PhotoViewModel(application, photoRepository, syncOrchestrator)
    }

    @After
    fun tearDown() {
        unmockkConstructor(PhotoCaptureHelper::class)
    }

    // --- getPhotosForEntity ---

    @Test
    fun `getPhotosForEntity returns flow from repository`() = runTest {
        val photos = listOf(
            mockk<PhotoEntity>(relaxed = true)
        )
        every { photoRepository.getPhotosForEntity("boat", "b1") } returns flowOf(photos)

        val result = viewModel.getPhotosForEntity("boat", "b1").first()

        assertEquals(1, result.size)
    }

    // --- savePhoto ---

    @Test
    fun `savePhoto success triggers sync and calls onSuccess`() = runTest {
        val uri = mockk<Uri>()
        val photo = mockk<PhotoEntity>(relaxed = true)
        coEvery { photoRepository.savePhoto("boat", "b1", uri, "image/jpeg") } returns photo
        every { syncOrchestrator.triggerImmediatePhotoSync() } returns Unit
        coEvery { photoRepository.getUnuploadedPhotoCount() } returns 0

        var successCalled = false
        viewModel.savePhoto("boat", "b1", uri, onSuccess = { successCalled = true })

        advanceUntilIdle()

        assertTrue(successCalled)
        verify { syncOrchestrator.triggerImmediatePhotoSync() }
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `savePhoto failure sets error and calls onError`() = runTest {
        val uri = mockk<Uri>()
        coEvery { photoRepository.savePhoto(any(), any(), any(), any()) } throws RuntimeException("Storage full")

        var errorMsg: String? = null
        viewModel.savePhoto("boat", "b1", uri, onError = { errorMsg = it })

        advanceUntilIdle()

        assertNotNull(errorMsg)
        assertTrue(errorMsg!!.contains("Storage full"))
        assertNotNull(viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    // --- deletePhoto ---

    @Test
    fun `deletePhoto success calls onSuccess`() = runTest {
        val photo = mockk<PhotoEntity>(relaxed = true)
        coEvery { photoRepository.deletePhoto(photo) } returns 1
        coEvery { photoRepository.getUnuploadedPhotoCount() } returns 0

        var successCalled = false
        viewModel.deletePhoto(photo, onSuccess = { successCalled = true })

        advanceUntilIdle()

        assertTrue(successCalled)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `deletePhoto failure sets error and calls onError`() = runTest {
        val photo = mockk<PhotoEntity>(relaxed = true)
        coEvery { photoRepository.deletePhoto(photo) } throws RuntimeException("Not found")

        var errorMsg: String? = null
        viewModel.deletePhoto(photo, onError = { errorMsg = it })

        advanceUntilIdle()

        assertNotNull(errorMsg)
        assertNotNull(viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    // --- triggerPhotoSync ---

    @Test
    fun `triggerPhotoSync calls syncOrchestrator`() {
        viewModel.triggerPhotoSync()

        verify { syncOrchestrator.triggerImmediatePhotoSync() }
    }

    // --- clearError ---

    @Test
    fun `clearError clears errorMessage`() = runTest {
        val uri = mockk<Uri>()
        coEvery { photoRepository.savePhoto(any(), any(), any(), any()) } throws RuntimeException("Fail")
        viewModel.savePhoto("boat", "b1", uri)
        advanceUntilIdle()
        assertNotNull(viewModel.errorMessage.value)

        viewModel.clearError()

        assertNull(viewModel.errorMessage.value)
    }

    // --- permission helpers ---

    @Test
    fun `hasCameraPermission delegates to helper`() {
        assertTrue(viewModel.hasCameraPermission())
    }

    @Test
    fun `hasStoragePermission delegates to helper`() {
        assertTrue(viewModel.hasStoragePermission())
    }
}
