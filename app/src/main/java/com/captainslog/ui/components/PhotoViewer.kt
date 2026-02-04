package com.captainslog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.captainslog.database.entities.PhotoEntity
import com.captainslog.util.PhotoSharingHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Full-screen photo viewer with zoom and swipe capabilities
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewer(
    photos: List<PhotoEntity>,
    initialPhotoIndex: Int = 0,
    onDismiss: () -> Unit,
    onDeletePhoto: (PhotoEntity) -> Unit = {},
    onSharePhoto: (PhotoEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf<PhotoEntity?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val photoSharingHelper = remember { PhotoSharingHelper(context) }
    
    if (photos.isEmpty()) {
        onDismiss()
        return
    }

    val pagerState = rememberPagerState(
        initialPage = initialPhotoIndex.coerceIn(0, photos.size - 1),
        pageCount = { photos.size }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Photo pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val photo = photos[page]
                ZoomableImage(
                    photo = photo,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Top bar with close button and photo info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${pagerState.currentPage + 1} of ${photos.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val currentPhoto = photos[pagerState.currentPage]
                    Text(
                        text = formatPhotoDate(currentPhoto.createdAt),
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    if (!currentPhoto.uploaded) {
                        Text(
                            text = "Upload pending",
                            color = Color(0xFFFF9500),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            // Bottom action bar
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Share button
                IconButton(
                    onClick = {
                        val currentPhoto = photos[pagerState.currentPage]
                        photoSharingHelper.sharePhoto(currentPhoto)
                    },
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }

                // Delete button
                IconButton(
                    onClick = {
                        showDeleteDialog = photos[pagerState.currentPage]
                    },
                    modifier = Modifier
                        .background(
                            Color.Red.copy(alpha = 0.7f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
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
                        onDeletePhoto(photo)
                        showDeleteDialog = null
                        
                        // If this was the last photo, close the viewer
                        if (photos.size == 1) {
                            onDismiss()
                        }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
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
}

/**
 * Zoomable image component with pinch-to-zoom and pan gestures
 */
@Composable
private fun ZoomableImage(
    photo: PhotoEntity,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(File(photo.localPath))
            .crossfade(true)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = "Photo",
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(0.5f, 5f)
                    
                    // Only allow panning if zoomed in
                    if (newScale > 1f) {
                        val maxX = (size.width * (newScale - 1)) / 2
                        val maxY = (size.height * (newScale - 1)) / 2
                        
                        offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                        offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                    
                    scale = newScale
                }
            }
            .clickable {
                // Double tap to reset zoom
                if (scale > 1f) {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                }
            },
        contentScale = ContentScale.Fit
    )
}

/**
 * Format photo creation date for display
 */
private fun formatPhotoDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    return formatter.format(date)
}