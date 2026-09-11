package com.cetin072.worknotebook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WorkItemEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class WorkNotebookDatabase : RoomDatabase() {
    abstract fun workItemDao(): WorkItemDao

    companion object {
        @Volatile
        private var INSTANCE: WorkNotebookDatabase? = null

        fun getInstance(context: Context): WorkNotebookDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WorkNotebookDatabase::class.java,
                    "work-notebook.db",
                ).build().also { INSTANCE = it }
            }
    }
}
