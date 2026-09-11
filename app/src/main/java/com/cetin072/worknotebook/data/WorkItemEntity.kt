package com.cetin072.worknotebook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work_items")
data class WorkItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val workDate: String?,
    val workTime: String?,
    val photoPath: String?,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
