package com.cetin072.worknotebook.domain

object WorkItemRules {
    private const val MAX_TITLE_LENGTH = 60

    fun makeTitle(explicitTitle: String, content: String): String {
        val requested = normalize(explicitTitle)
        if (requested.isNotBlank()) return requested.take(MAX_TITLE_LENGTH)

        val normalizedContent = normalize(content)
        if (normalizedContent.length <= MAX_TITLE_LENGTH) return normalizedContent
        return normalizedContent.take(MAX_TITLE_LENGTH - 1).trimEnd() + "…"
    }

    fun isTodayPending(workDate: String?, isCompleted: Boolean, today: String): Boolean =
        !isCompleted && workDate == today

    private fun normalize(value: String): String =
        value.replace(Regex("\\s+"), " ").trim()
}
