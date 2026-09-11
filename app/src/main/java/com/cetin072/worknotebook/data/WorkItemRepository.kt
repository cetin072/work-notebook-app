package com.cetin072.worknotebook.data

import kotlinx.coroutines.flow.Flow

class WorkItemRepository(private val dao: WorkItemDao) {
    fun observeAll(): Flow<List<WorkItemEntity>> = dao.observeAll()

    suspend fun getTodayPending(today: String): List<WorkItemEntity> = dao.getTodayPending(today)

    suspend fun upsert(item: WorkItemEntity) = dao.upsert(item)

    suspend fun setCompleted(id: String, completed: Boolean) =
        dao.setCompleted(id, completed, System.currentTimeMillis())
}
