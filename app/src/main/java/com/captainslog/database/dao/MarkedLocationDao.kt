package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.MarkedLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkedLocationDao {
    
    @Query("SELECT * FROM marked_locations ORDER BY name ASC")
    fun getAllMarkedLocations(): Flow<List<MarkedLocationEntity>>
    
    @Query("SELECT * FROM marked_locations WHERE id = :id")
    suspend fun getMarkedLocationById(id: String): MarkedLocationEntity?
    
    @Query("SELECT * FROM marked_locations WHERE category = :category ORDER BY name ASC")
    fun getMarkedLocationsByCategory(category: String): Flow<List<MarkedLocationEntity>>
    
    @Query("SELECT * FROM marked_locations WHERE tags LIKE '%' || :tag || '%' ORDER BY name ASC")
    fun getMarkedLocationsByTag(tag: String): Flow<List<MarkedLocationEntity>>
    
    @Query("SELECT * FROM marked_locations WHERE name LIKE '%' || :search || '%' OR notes LIKE '%' || :search || '%' ORDER BY name ASC")
    fun searchMarkedLocations(search: String): Flow<List<MarkedLocationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarkedLocation(location: MarkedLocationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarkedLocations(locations: List<MarkedLocationEntity>)
    
    @Update
    suspend fun updateMarkedLocation(location: MarkedLocationEntity)
    
    @Delete
    suspend fun deleteMarkedLocation(location: MarkedLocationEntity)
    
    @Query("DELETE FROM marked_locations WHERE id = :id")
    suspend fun deleteMarkedLocationById(id: String)
    
    @Query("DELETE FROM marked_locations")
    suspend fun deleteAllMarkedLocations()
    
    @Query("SELECT DISTINCT category FROM marked_locations ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>
    
    @Query("SELECT DISTINCT tags FROM marked_locations WHERE tags != ''")
    suspend fun getAllTags(): List<String>

    @Query("SELECT * FROM marked_locations ORDER BY name ASC")
    suspend fun getAllMarkedLocationsSync(): List<MarkedLocationEntity>
}