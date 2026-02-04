package com.captainslog.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.captainslog.database.entities.PhotoEntity
import com.captainslog.util.PhotoSharingHelper
import com.captainslog.util.rememberPhotoCaptureState
import com.captainslog.viewmodel.PhotoViewModel
import java.io.File

/**
 * Composable component for photo capture and display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCaptureComponent(
    entityType: String,
    entityId: String,
    modifier: Modifier = Modifier,
    photoViewModel: PhotoViewModel = viewModel()
) {
    val context = LocalContext.current
    val photoSharingHelper = remember { PhotoSharingHelper(context) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<PhotoEntity?>(null) }
    var showPhotoViewer by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }

    // Collect photos for this entity
    val photos by photoViewModel.getPhotosForEntity(entityType, entityId).collectAsState(initial = emptyList())
    val isLoading by photoViewModel.isLoading.collectAsState()
    val errorMessage by photoViewModel.errorMessage.collectAsState()
    val unuploadedCount by photoViewModel.unuploadedPhotoCount.collectAsState()

    // Photo capture state
    val photoCaptureState = rememberPhotoCaptureState(
        onPhotoTaken = { uri ->
            photoViewModel.savePhoto(
                entityType = entityType,
                entityId = entityId,
                imageUri = uri,
                onSuccess = { photo ->
                    // Photo saved successfully
                },
                onError = { error ->
                    // Error handled by ViewModel
                }
            )
        },
        onPermissionDenied = {
            // Handle permission denied
        }
    )

    Column(modifier = modifier) {
        // Header with add button and upload status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Photos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Upload status indicator
                if (unuploadedCount > 0) {
                    Badge(
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("$unuploadedCount pending")
                    }
                }

                // Add photo button
                IconButton(
                    onClick = { showBottomSheet = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Photo"
                    )
                }
            }
        }

        // Error message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { photoViewModel.clearError() }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Photos grid
        if (photos.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(photos) { photo ->
                    val photoIndex = photos.indexOf(photo)
                    PhotoItem(
                        photo = photo,
                        onDelete = { showDeleteDialog = photo },
                        onView = {
                            selectedPhotoIndex = photoIndex
                            showPhotoViewer = true
                        },
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        } else {
            // Empty state
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { showBottomSheet = true },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No photos yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap to add photos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Bottom sheet for photo options
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Add Photo",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Camera option
                ListItem(
                    headlineContent = { Text("Take Photo") },
                    supportingContent = { Text("Use camera to take a new photo") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        photoCaptureState.launchCamera()
                    }
                )

                // Gallery option
                ListItem(
                    headlineContent = { Text("Choose from Gallery") },
                    supportingContent = { Text("Select an existing photo") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        photoCaptureState.launchGallery()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { photo ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Photo") },
            text = { Text("Are you sure you want to delete this photo? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        photoViewModel.deletePhoto(
                            photo = photo,
                            onSuccess = { showDeleteDialog = null },
                            onError = { showDeleteDialog = null }
                        )
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Photo viewer
    if (showPhotoViewer && photos.isNotEmpty()) {
        PhotoViewer(
            photos = photos,
            initialPhotoIndex = selectedPhotoIndex,
            onDismiss = { showPhotoViewer = false },
            onDeletePhoto = { photo ->
                photoViewModel.deletePhoto(
                    photo = photo,
                    onSuccess = { 
                        // If this was the last photo, close the viewer
                        if (photos.size <= 1) {
                            showPhotoViewer = false
                        }
                    },
                    onError = { }
                )
            },
            onSharePhoto = { photo ->
                photoSharingHelper.sharePhoto(photo)
            }
        )
    }
}

/**
 * Individual photo item component
 */
@Composable
private fun PhotoItem(
    photo: PhotoEntity,
    onDelete: () -> Unit,
    onView: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onView() }
    ) {
        // Photo image
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(File(photo.localPath))
                .crossfade(true)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "Photo",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        // Upload status indicator
        if (!photo.uploaded) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(
                        Color(0xFFFF9500).copy(alpha = 0.8f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(50)
                )
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Photo",
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        }
    }
}