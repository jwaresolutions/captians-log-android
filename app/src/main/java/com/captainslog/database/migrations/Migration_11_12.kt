package com.captainslog.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Boat vessel detail columns for CG-719S
        db.execSQL("ALTER TABLE boats ADD COLUMN officialNumber TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN grossTons REAL DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN lengthFeet INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN lengthInches INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN widthFeet INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN widthInches INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN depthFeet INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN depthInches INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN propulsionType TEXT DEFAULT NULL")

        // Boat owner/operator columns for CG-719S
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerFirstName TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerMiddleName TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerLastName TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerStreetAddress TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerCity TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerState TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerZipCode TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerEmail TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE boats ADD COLUMN ownerPhone TEXT DEFAULT NULL")

        // Trip CG-719S columns
        db.execSQL("ALTER TABLE trips ADD COLUMN bodyOfWater TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE trips ADD COLUMN boundaryClassification TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE trips ADD COLUMN distanceOffshore REAL DEFAULT NULL")

        // Migrate trip roles to USCG terminology
        db.execSQL("UPDATE trips SET role = 'master' WHERE role = 'captain'")
        db.execSQL("UPDATE trips SET role = 'deckhand' WHERE role = 'crew'")
        db.execSQL("UPDATE trips SET role = 'other' WHERE role = 'observer'")
    }
}
