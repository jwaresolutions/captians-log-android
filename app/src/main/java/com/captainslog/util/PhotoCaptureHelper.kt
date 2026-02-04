package com.captainslog.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for handling camera permissions and photo capture
 */
class PhotoCaptureHelper(private val context: Context) {

    companion object {
        const val TAG = "PhotoCaptureHelper"
        private const val PHOTOS_TEMP_DIR = "temp_photos"
    }

    /**
     * Check if camera permission is granted
     */
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if storage permission is granted (for Android < 10)
     */
    fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ doesn't need storage permission for app-specific directories
            true
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Get required permissions for photo capture
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.CAMERA)
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Create a temporary file URI for camera capture
     */
    fun createTempImageUri(): Uri {
        val tempDir = File(context.filesDir, PHOTOS_TEMP_DIR)
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(tempDir, "TEMP_${timeStamp}.jpg")

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    /**
     * Clean up temporary photo files
     */
    fun cleanupTempFiles() {
        try {
            val tempDir = File(context.filesDir, PHOTOS_TEMP_DIR)
            if (tempDir.exists()) {
                tempDir.listFiles()?.forEach { file ->
                    if (file.isFile && file.name.startsWith("TEMP_")) {
                        // Delete files older than 1 hour
                        val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
                        if (file.lastModified() < oneHourAgo) {
                            file.delete()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error cleaning up temp files", e)
        }
    }

    /**
     * Get MIME type from file extension
     */
    fun getMimeTypeFromUri(uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "image/jpeg"
    }
}

/**
 * Composable function to create photo capture launchers
 */
@Composable
fun rememberPhotoCaptureState(
    onPhotoTaken: (Uri) -> Unit,
    onPermissionDenied: () -> Unit = {}
): PhotoCaptureState {
    val context = LocalContext.current
    val helper = remember { PhotoCaptureHelper(context) }

    // Camera launcher
    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Photo was taken successfully
            // The URI is already set in the state
        }
    }

    // Gallery launcher
    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onPhotoTaken(it) }
    }

    // Permission launcher
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            onPermissionDenied()
        }
    }

    return remember {
        PhotoCaptureState(
            helper = helper,
            cameraLauncher = cameraLauncher,
            galleryLauncher = galleryLauncher,
            permissionLauncher = permissionLauncher,
            onPhotoTaken = onPhotoTaken
        )
    }
}

/**
 * State holder for photo capture functionality
 */
class PhotoCaptureState(
    private val helper: PhotoCaptureHelper,
    private val cameraLauncher: ActivityResultLauncher<Uri>,
    private val galleryLauncher: ActivityResultLauncher<String>,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>,
    private val onPhotoTaken: (Uri) -> Unit
) {
    private var pendingCameraUri: Uri? = null

    /**
     * Launch camera to take a photo
     */
    fun launchCamera() {
        if (helper.hasCameraPermission() && helper.hasStoragePermission()) {
            val uri = helper.createTempImageUri()
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            // Request permissions
            permissionLauncher.launch(helper.getRequiredPermissions())
        }
    }

    /**
     * Launch gallery to select a photo
     */
    fun launchGallery() {
        galleryLauncher.launch("image/*")
    }

    /**
     * Check if permissions are granted
     */
    fun hasPermissions(): Boolean {
        return helper.hasCameraPermission() && helper.hasStoragePermission()
    }

    /**
     * Handle camera result (called internally)
     */
    fun handleCameraResult(success: Boolean) {
        if (success && pendingCameraUri != null) {
            onPhotoTaken(pendingCameraUri!!)
            pendingCameraUri = null
        }
    }

    /**
     * Get MIME type from URI
     */
    fun getMimeType(uri: Uri): String {
        return helper.getMimeTypeFromUri(uri)
    }

    /**
     * Clean up temporary files
     */
    fun cleanup() {
        helper.cleanupTempFiles()
    }
}