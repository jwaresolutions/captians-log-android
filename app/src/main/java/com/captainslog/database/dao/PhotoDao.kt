package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.PhotoEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE entityType = :entityType AND entityId = :entityId")
    fun getPhotosForEntity(entityType: String, entityId: String): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE entityType = :entityType AND entityId = :entityId")
    suspend fun getPhotosForEntitySync(entityType: String, entityId: String): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE uploaded = 0")
    suspend fun getUnuploadedPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE uploaded = 1 AND uploadedAt < :cutoffDate")
    suspend fun getOldUploadedPhotos(cutoffDate: Date): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Update
    suspend fun updatePhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: String): PhotoEntity?

    @Query("UPDATE photos SET uploaded = 1, uploadedAt = :uploadedAt WHERE id = :photoId")
    suspend fun markAsUploaded(photoId: String, uploadedAt: Date)

    @Query("SELECT * FROM photos")
    suspend fun getAllPhotosSync(): List<PhotoEntity>
}
