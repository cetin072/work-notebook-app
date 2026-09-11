package com.cetin072.worknotebook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [WorkItemEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class WorkNotebookDatabase : RoomDatabase() {
    abstract fun workItemDao(): WorkItemDao

    companion object {
        @Volatile
        private var INSTANCE: WorkNotebookDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE work_items ADD COLUMN photoPath TEXT")
            }
        }

        fun getInstance(context: Context): WorkNotebookDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WorkNotebookDatabase::class.java,
                    "work-notebook.db",
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
