package com.captainslog.network.models

import com.google.gson.annotations.SerializedName

/**
 * Request model for uploading a photo
 */
data class UploadPhotoRequest(
    @SerializedName("entityType")
    val entityType: String, // trip, maintenance, note
    
    @SerializedName("entityId")
    val entityId: String
)

/**
 * Response model for photo operations
 */
data class PhotoResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("entityType")
    val entityType: String,
    
    @SerializedName("entityId")
    val entityId: String,
    
    @SerializedName("originalPath")
    val originalPath: String,
    
    @SerializedName("webOptimizedPath")
    val webOptimizedPath: String,
    
    @SerializedName("mimeType")
    val mimeType: String,
    
    @SerializedName("sizeBytes")
    val sizeBytes: Long,
    
    @SerializedName("metadata")
    val metadata: PhotoMetadata?,
    
    @SerializedName("createdAt")
    val createdAt: String
)

/**
 * Photo metadata
 */
data class PhotoMetadata(
    @SerializedName("width")
    val width: Int?,
    
    @SerializedName("height")
    val height: Int?,
    
    @SerializedName("takenAt")
    val takenAt: String?
)

/**
 * Response for listing photos
 */
data class PhotoListResponse(
    @SerializedName("data")
    val data: List<PhotoResponse>,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("timestamp")
    val timestamp: String
)