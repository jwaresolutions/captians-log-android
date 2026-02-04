package com.captainslog.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Boats - add ownership and origin tracking fields
        database.execSQL("ALTER TABLE boats ADD COLUMN ownerId TEXT")
        database.execSQL("ALTER TABLE boats ADD COLUMN originSource TEXT")
        database.execSQL("ALTER TABLE boats ADD COLUMN originTimestamp INTEGER")

        // Trips - add captain ownership, origin tracking, and read-only flag
        database.execSQL("ALTER TABLE trips ADD COLUMN captainId TEXT")
        database.execSQL("ALTER TABLE trips ADD COLUMN originSource TEXT")
        database.execSQL("ALTER TABLE trips ADD COLUMN originTimestamp INTEGER")
        database.execSQL("ALTER TABLE trips ADD COLUMN isReadOnly INTEGER NOT NULL DEFAULT 0")

        // Notes - add origin tracking
        database.execSQL("ALTER TABLE notes ADD COLUMN originSource TEXT")
        database.execSQL("ALTER TABLE notes ADD COLUMN originTimestamp INTEGER")

        // Photos - add origin tracking
        database.execSQL("ALTER TABLE photos ADD COLUMN originSource TEXT")
        database.execSQL("ALTER TABLE photos ADD COLUMN originTimestamp INTEGER")

        // Marked locations - add origin tracking
        database.execSQL("ALTER TABLE marked_locations ADD COLUMN originSource TEXT")
        database.execSQL("ALTER TABLE marked_locations ADD COLUMN originTimestamp INTEGER")

        // Todo lists - add origin tracking
        database.execSQL("ALTER TABLE todo_lists ADD COLUMN originSource TEXT")
        database.execSQL("ALTER TABLE todo_lists ADD COLUMN originTimestamp INTEGER")
    }
}
