package com.cetin072.worknotebook.domain

object WorkItemRules {
    private const val MAX_TITLE_LENGTH = 60
    private val splitMarker = Regex("\\s*(?:다음\\s*업무|다음\\s*건|그다음)\\s*")

    fun makeTitle(explicitTitle: String, content: String): String {
        val requested = normalize(explicitTitle)
        if (requested.isNotBlank()) return requested.take(MAX_TITLE_LENGTH)

        val normalizedContent = normalize(content)
        if (normalizedContent.length <= MAX_TITLE_LENGTH) return normalizedContent
        return normalizedContent.take(MAX_TITLE_LENGTH - 1).trimEnd() + "…"
    }

    fun splitWorkSegments(content: String): List<String> {
        val normalized = normalize(content)
        if (normalized.isBlank()) return emptyList()
        if (!splitMarker.containsMatchIn(normalized)) return listOf(normalized)

        return splitMarker
            .split(normalized)
            .map(::normalize)
            .filter { it.isNotBlank() }
    }

    fun isTodayPending(workDate: String?, isCompleted: Boolean, today: String): Boolean =
        !isCompleted && workDate == today

    private fun normalize(value: String): String =
        value.replace(Regex("\\s+"), " ").trim()
}
