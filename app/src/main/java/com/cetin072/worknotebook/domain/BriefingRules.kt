package com.cetin072.worknotebook.domain

import com.cetin072.worknotebook.data.WorkItemEntity

data class BriefingSummary(
    val overdueCount: Int,
    val todayCount: Int,
    val topItems: List<WorkItemEntity>,
) {
    val totalActionCount: Int
        get() = overdueCount + todayCount
}

object BriefingRules {
    fun build(
        items: List<WorkItemEntity>,
        today: String,
        limit: Int = 3,
    ): BriefingSummary {
        val actionable = items.filter { item ->
            !item.isCompleted && item.workDate != null && item.workDate <= today
        }

        val overdueCount = actionable.count { it.workDate!! < today }
        val todayCount = actionable.count { it.workDate == today }

        val ordered = actionable.sortedWith(
            compareBy<WorkItemEntity>(
                { if (it.workDate!! < today) 0 else 1 },
                { it.workDate },
                { if (it.workTime == null) 1 else 0 },
                { it.workTime ?: "" },
                { it.createdAt },
            )
        )

        return BriefingSummary(
            overdueCount = overdueCount,
            todayCount = todayCount,
            topItems = ordered.take(limit.coerceAtLeast(0)),
        )
    }
}
