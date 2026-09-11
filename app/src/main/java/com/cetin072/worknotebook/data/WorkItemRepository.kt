package com.cetin072.worknotebook.data

import kotlinx.coroutines.flow.Flow

class WorkItemRepository(private val dao: WorkItemDao) {
    fun observeAll(): Flow<List<WorkItemEntity>> = dao.observeAll()

    suspend fun getBriefingPending(today: String): List<WorkItemEntity> = dao.getBriefingPending(today)

    suspend fun upsert(item: WorkItemEntity) = dao.upsert(item)

    suspend fun setCompleted(id: String, completed: Boolean) =
        dao.setCompleted(id, completed, System.currentTimeMillis())
}
