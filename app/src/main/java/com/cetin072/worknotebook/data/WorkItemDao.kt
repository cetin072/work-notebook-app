package com.cetin072.worknotebook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkItemDao {
    @Query(
        """
        SELECT * FROM work_items
        ORDER BY
            isCompleted ASC,
            CASE WHEN workDate IS NULL THEN 1 ELSE 0 END ASC,
            workDate ASC,
            CASE WHEN workTime IS NULL THEN 1 ELSE 0 END ASC,
            workTime ASC,
            createdAt DESC
        """
    )
    fun observeAll(): Flow<List<WorkItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WorkItemEntity)

    @Query("UPDATE work_items SET isCompleted = :completed, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setCompleted(id: String, completed: Boolean, updatedAt: Long)
}
