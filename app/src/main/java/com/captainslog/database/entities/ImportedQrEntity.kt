package com.captainslog.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.captainslog.database.converters.DateConverter
import java.util.Date

/**
 * Tracks imported QR codes to prevent duplicate imports
 *
 * Stores the QR envelope ID after successful import, so the app can detect
 * and prevent re-importing the same data if the user scans the same QR again.
 */
@Entity(tableName = "imported_qr_codes")
@TypeConverters(DateConverter::class)
data class ImportedQrEntity(
    /**
     * QR envelope ID (UUID from web generator)
     * This is unique per QR code generation, even if content is identical
     */
    @PrimaryKey
    val qrId: String,

    /**
     * Type of data imported ("trip" or "boat")
     */
    val type: String,

    /**
     * Timestamp when this QR was imported
     */
    val importedAt: Date,

    /**
     * Number of trips imported from this QR
     * - For type="trip": number of trips in the batch
     * - For type="boat": always 0
     */
    val tripCount: Int = 0
)
