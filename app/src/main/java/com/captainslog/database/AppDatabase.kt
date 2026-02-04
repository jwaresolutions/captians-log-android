package com.captainslog.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.captainslog.database.converters.DateConverter
import com.captainslog.database.dao.BoatDao
import com.captainslog.database.dao.GpsPointDao
import com.captainslog.database.dao.MaintenanceTemplateDao
import com.captainslog.database.dao.MaintenanceEventDao
import com.captainslog.database.dao.MarkedLocationDao
import com.captainslog.database.dao.NoteDao
import com.captainslog.database.dao.PhotoDao
import com.captainslog.database.dao.TodoItemDao
import com.captainslog.database.dao.TodoListDao
import com.captainslog.database.dao.TripDao
import com.captainslog.database.dao.OfflineChangeDao
import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.GpsPointEntity
import com.captainslog.database.entities.MaintenanceTemplateEntity
import com.captainslog.database.entities.MaintenanceEventEntity
import com.captainslog.database.entities.MarkedLocationEntity
import com.captainslog.database.entities.NoteEntity
import com.captainslog.database.entities.PhotoEntity
import com.captainslog.database.entities.TodoItemEntity
import com.captainslog.database.entities.TodoListEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.database.entities.OfflineChangeEntity
import com.captainslog.database.migrations.MIGRATION_9_10

@Database(
    entities = [
        TripEntity::class,
        GpsPointEntity::class,
        PhotoEntity::class,
        BoatEntity::class,
        NoteEntity::class,
        TodoListEntity::class,
        TodoItemEntity::class,
        MaintenanceTemplateEntity::class,
        MaintenanceEventEntity::class,
        MarkedLocationEntity::class,
        OfflineChangeEntity::class
    ],
    version = 10,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun gpsPointDao(): GpsPointDao
    abstract fun photoDao(): PhotoDao
    abstract fun boatDao(): BoatDao
    abstract fun noteDao(): NoteDao
    abstract fun todoListDao(): TodoListDao
    abstract fun todoItemDao(): TodoItemDao
    abstract fun maintenanceTemplateDao(): MaintenanceTemplateDao
    abstract fun maintenanceEventDao(): MaintenanceEventDao
    abstract fun markedLocationDao(): MarkedLocationDao
    abstract fun offlineChangeDao(): OfflineChangeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "boat_tracking_database"
                )
                    .addMigrations(MIGRATION_9_10)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return getDatabase(context)
        }
    }
}
