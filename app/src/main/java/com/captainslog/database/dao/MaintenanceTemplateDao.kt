package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.MaintenanceTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceTemplateDao {
    @Query("SELECT * FROM maintenance_templates WHERE isActive = 1 ORDER BY title ASC")
    fun getAllActiveTemplates(): Flow<List<MaintenanceTemplateEntity>>

    @Query("SELECT * FROM maintenance_templates WHERE boatId = :boatId AND isActive = 1 ORDER BY title ASC")
    fun getTemplatesByBoat(boatId: String): Flow<List<MaintenanceTemplateEntity>>

    @Query("SELECT * FROM maintenance_templates WHERE id = :id")
    fun getTemplateById(id: String): Flow<MaintenanceTemplateEntity?>

    @Query("SELECT * FROM maintenance_templates WHERE id = :id")
    suspend fun getTemplateByIdSync(id: String): MaintenanceTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: MaintenanceTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<MaintenanceTemplateEntity>)

    @Update
    suspend fun updateTemplate(template: MaintenanceTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: MaintenanceTemplateEntity)

    @Query("DELETE FROM maintenance_templates WHERE id = :id")
    suspend fun deleteTemplateById(id: String)

    @Query("UPDATE maintenance_templates SET isActive = :isActive WHERE id = :id")
    suspend fun updateTemplateActiveStatus(id: String, isActive: Boolean)

    @Query("DELETE FROM maintenance_templates")
    suspend fun deleteAllTemplates()

    @Query("SELECT * FROM maintenance_templates")
    suspend fun getAllTemplatesSync(): List<MaintenanceTemplateEntity>
}