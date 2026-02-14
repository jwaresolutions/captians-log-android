package com.captainslog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.captainslog.database.entities.PhotoEntity
import com.captainslog.util.PhotoSharingHelper
import com.captainslog.viewmodel.PhotoViewModel
import java.io.File

/**
 * Compact photo grid component for displaying photos in a grid layout
 */
@Composable
fun PhotoGrid(
    entityType: String,
    entityId: String,
    modifier: Modifier = Modifier,
    maxPhotosToShow: Int = 6,
    photoSize: androidx.compose.ui.unit.Dp = 80.dp,
    showTitle: Boolean = true,
    photoViewModel: PhotoViewModel = hiltViewModel()
) {
    // Collect photos for this entity
    val photos by photoViewModel.getPhotosForEntity(entityType, entityId).collectAsState(initial = emptyList())
    val context = LocalContext.current
    val photoSharingHelper = remember { PhotoSharingHelper(context) }
    var showPhotoViewer by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }

    if (photos.isEmpty()) {
        return
    }

    Column(modifier = modifier) {
        if (showTitle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Photos (${photos.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                if (photos.size > maxPhotosToShow) {
                    Text(
                        text = "+${photos.size - maxPhotosToShow} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Photo grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = photoSize),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val photosToShow = photos.take(maxPhotosToShow)
            items(photosToShow) { photo ->
                val photoIndex = photos.indexOf(photo)
                PhotoGridItem(
                    photo = photo,
                    onClick = {
                        selectedPhotoIndex = photoIndex
                        showPhotoViewer = true
                    },
                    modifier = Modifier.size(photoSize)
                )
            }
        }
    }

    // Photo viewer
    if (showPhotoViewer) {
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
 * Individual photo item in the grid
 */
@Composable
private fun PhotoGridItem(
    photo: PhotoEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
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
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )

        // Upload status indicator (smaller for grid)
        if (!photo.uploaded) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .background(
                        Color(0xFFFF9500).copy(alpha = 0.9f),
                        RoundedCornerShape(3.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Upload pending",
                    modifier = Modifier.size(8.dp),
                    tint = Color.White
                )
            }
        }
    }
}