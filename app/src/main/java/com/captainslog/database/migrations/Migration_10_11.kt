package com.captainslog.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create crew_members table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS crew_members (
                tripId TEXT NOT NULL,
                deviceId TEXT NOT NULL,
                displayName TEXT NOT NULL,
                joinedAt INTEGER NOT NULL,
                role TEXT NOT NULL DEFAULT 'crew',
                PRIMARY KEY(tripId, deviceId)
            )
        """.trimIndent())

        // Add new columns to trips table
        db.execSQL("ALTER TABLE trips ADD COLUMN captainTripId TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE trips ADD COLUMN captainName TEXT DEFAULT NULL")
    }
}
