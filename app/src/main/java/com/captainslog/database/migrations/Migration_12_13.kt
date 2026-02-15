package com.captainslog.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create imported_qr_codes table for duplicate detection
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS imported_qr_codes (
                qrId TEXT PRIMARY KEY NOT NULL,
                type TEXT NOT NULL,
                importedAt INTEGER NOT NULL,
                tripCount INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
    }
}
