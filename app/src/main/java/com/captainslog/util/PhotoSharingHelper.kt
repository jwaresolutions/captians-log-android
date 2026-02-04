package com.captainslog.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.captainslog.database.entities.PhotoEntity
import java.io.File

/**
 * Helper class for sharing photos
 */
class PhotoSharingHelper(private val context: Context) {

    /**
     * Share a photo using the system share intent
     */
    fun sharePhoto(photo: PhotoEntity) {
        try {
            val photoFile = File(photo.localPath)
            if (!photoFile.exists()) {
                return
            }

            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = photo.mimeType
                putExtra(Intent.EXTRA_STREAM, photoUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share Photo")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            android.util.Log.e("PhotoSharingHelper", "Error sharing photo", e)
        }
    }

    /**
     * Share multiple photos
     */
    fun sharePhotos(photos: List<PhotoEntity>) {
        try {
            if (photos.isEmpty()) return

            val photoUris = photos.mapNotNull { photo ->
                val photoFile = File(photo.localPath)
                if (photoFile.exists()) {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                } else {
                    null
                }
            }

            if (photoUris.isEmpty()) return

            val shareIntent = Intent().apply {
                if (photoUris.size == 1) {
                    action = Intent.ACTION_SEND
                    type = photos.first().mimeType
                    putExtra(Intent.EXTRA_STREAM, photoUris.first())
                } else {
                    action = Intent.ACTION_SEND_MULTIPLE
                    type = "image/*"
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(photoUris))
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(
                shareIntent, 
                if (photoUris.size == 1) "Share Photo" else "Share ${photoUris.size} Photos"
            )
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            android.util.Log.e("PhotoSharingHelper", "Error sharing photos", e)
        }
    }
}