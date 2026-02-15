package com.captainslog.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.captainslog.database.entities.ImportedQrEntity

/**
 * Data Access Object for tracking imported QR codes
 */
@Dao
interface ImportedQrDao {
    /**
     * Check if a QR code has already been imported
     * @param qrId QR envelope ID from scanned code
     * @return ImportedQrEntity if previously imported, null otherwise
     */
    @Query("SELECT * FROM imported_qr_codes WHERE qrId = :qrId")
    suspend fun getByQrId(qrId: String): ImportedQrEntity?

    /**
     * Record a successfully imported QR code
     * @param entity Import record to store
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ImportedQrEntity)

    /**
     * Get all imported QR codes (for debugging/admin)
     * @return List of all import records
     */
    @Query("SELECT * FROM imported_qr_codes ORDER BY importedAt DESC")
    suspend fun getAll(): List<ImportedQrEntity>

    /**
     * Delete old import records (optional cleanup)
     * @param cutoffDate Delete records older than this date
     */
    @Query("DELETE FROM imported_qr_codes WHERE importedAt < :cutoffDate")
    suspend fun deleteOlderThan(cutoffDate: java.util.Date)
}
