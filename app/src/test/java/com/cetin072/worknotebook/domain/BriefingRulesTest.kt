package com.cetin072.worknotebook.domain

import com.cetin072.worknotebook.data.WorkItemEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class BriefingRulesTest {
    @Test
    fun `overdue comes before today's timed and untimed tasks`() {
        val summary = BriefingRules.build(
            items = listOf(
                item(id = "today-untimed", date = "2026-09-11", time = null),
                item(id = "today-1500", date = "2026-09-11", time = "15:00"),
                item(id = "overdue", date = "2026-09-10", time = null),
                item(id = "today-0900", date = "2026-09-11", time = "09:00"),
            ),
            today = "2026-09-11",
        )

        assertEquals(1, summary.overdueCount)
        assertEquals(3, summary.todayCount)
        assertEquals(listOf("overdue", "today-0900", "today-1500"), summary.topItems.map { it.id })
    }

    @Test
    fun `completed future and undated records are excluded`() {
        val summary = BriefingRules.build(
            items = listOf(
                item(id = "done", date = "2026-09-10", completed = true),
                item(id = "future", date = "2026-09-12"),
                item(id = "memo", date = null),
                item(id = "today", date = "2026-09-11"),
            ),
            today = "2026-09-11",
        )

        assertEquals(0, summary.overdueCount)
        assertEquals(1, summary.todayCount)
        assertEquals(listOf("today"), summary.topItems.map { it.id })
    }

    @Test
    fun `limit is respected`() {
        val summary = BriefingRules.build(
            items = (1..5).map { index ->
                item(id = "task-$index", date = "2026-09-11", time = "0$index:00")
            },
            today = "2026-09-11",
            limit = 2,
        )

        assertEquals(2, summary.topItems.size)
        assertEquals(5, summary.totalActionCount)
    }

    private fun item(
        id: String,
        date: String?,
        time: String? = null,
        completed: Boolean = false,
    ) = WorkItemEntity(
        id = id,
        title = id,
        content = id,
        workDate = date,
        workTime = time,
        photoPath = null,
        isCompleted = completed,
        createdAt = 1L,
        updatedAt = 1L,
    )
}
