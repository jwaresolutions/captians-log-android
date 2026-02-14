package com.captainslog.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.database.entities.PhotoEntity
import com.captainslog.repository.PhotoRepository
import com.captainslog.sync.SyncOrchestrator
import com.captainslog.util.PhotoCaptureHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing photo operations
 */
@HiltViewModel
class PhotoViewModel @Inject constructor(
    application: Application,
    private val photoRepository: PhotoRepository,
    private val syncOrchestrator: SyncOrchestrator
) : AndroidViewModel(application) {

    private val photoCaptureHelper = PhotoCaptureHelper(application)

    companion object {
        const val TAG = "PhotoViewModel"
    }

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _unuploadedPhotoCount = MutableStateFlow(0)
    val unuploadedPhotoCount: StateFlow<Int> = _unuploadedPhotoCount.asStateFlow()

    init {
        // Monitor unuploaded photo count
        viewModelScope.launch {
            while (true) {
                try {
                    val count = photoRepository.getUnuploadedPhotoCount()
                    _unuploadedPhotoCount.value = count
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting unuploaded photo count", e)
                }
                kotlinx.coroutines.delay(30000) // Check every 30 seconds
            }
        }

        // Clean up temp files periodically
        viewModelScope.launch {
            photoCaptureHelper.cleanupTempFiles()
        }
    }

    /**
     * Get photos for a specific entity
     */
    fun getPhotosForEntity(entityType: String, entityId: String): Flow<List<PhotoEntity>> {
        return photoRepository.getPhotosForEntity(entityType, entityId)
    }

    /**
     * Save a photo from camera or gallery
     */
    fun savePhoto(
        entityType: String,
        entityId: String,
        imageUri: Uri,
        onSuccess: (PhotoEntity) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val mimeType = photoCaptureHelper.getMimeTypeFromUri(imageUri)
                val photoEntity = photoRepository.savePhoto(entityType, entityId, imageUri, mimeType)

                Log.d(TAG, "Photo saved: ${photoEntity.id}")
                onSuccess(photoEntity)

                // Trigger immediate photo sync if on WiFi
                syncOrchestrator.triggerImmediatePhotoSync()

                // Update unuploaded count
                _unuploadedPhotoCount.value = photoRepository.getUnuploadedPhotoCount()

            } catch (e: Exception) {
                Log.e(TAG, "Error saving photo", e)
                val errorMsg = "Failed to save photo: ${e.message}"
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete a photo
     */
    fun deletePhoto(
        photo: PhotoEntity,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                photoRepository.deletePhoto(photo)
                Log.d(TAG, "Photo deleted: ${photo.id}")
                onSuccess()

                // Update unuploaded count
                _unuploadedPhotoCount.value = photoRepository.getUnuploadedPhotoCount()

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting photo", e)
                val errorMsg = "Failed to delete photo: ${e.message}"
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Trigger immediate photo sync
     */
    fun triggerPhotoSync() {
        syncOrchestrator.triggerImmediatePhotoSync()
        Log.d(TAG, "Triggered immediate photo sync")
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Check if camera permission is granted
     */
    fun hasCameraPermission(): Boolean {
        return photoCaptureHelper.hasCameraPermission()
    }

    /**
     * Check if storage permission is granted
     */
    fun hasStoragePermission(): Boolean {
        return photoCaptureHelper.hasStoragePermission()
    }

    /**
     * Get required permissions
     */
    fun getRequiredPermissions(): Array<String> {
        return photoCaptureHelper.getRequiredPermissions()
    }

    /**
     * Get MIME type from URI
     */
    fun getMimeType(uri: Uri): String {
        return photoCaptureHelper.getMimeTypeFromUri(uri)
    }

    /**
     * Create temp image URI for camera
     */
    fun createTempImageUri(): Uri {
        return photoCaptureHelper.createTempImageUri()
    }

    /**
     * Clean up temporary files
     */
    fun cleanupTempFiles() {
        viewModelScope.launch {
            photoCaptureHelper.cleanupTempFiles()
        }
    }
}
