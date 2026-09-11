package com.cetin072.worknotebook.domain

object SpeechTextMerger {
    fun normalize(text: String?): String = text
        .orEmpty()
        .replace(Regex("\\s+"), " ")
        .trim()

    fun merge(base: String?, addition: String?): String {
        val left = normalize(base)
        val right = normalize(addition)

        if (left.isBlank()) return right
        if (right.isBlank()) return left
        if (left == right) return left
        if (right.startsWith("$left ")) return right
        if (left.endsWith(" $right")) return left

        val leftWords = left.split(" ")
        val rightWords = right.split(" ")
        val maxOverlap = minOf(leftWords.size, rightWords.size, 80)

        for (size in maxOverlap downTo 1) {
            val leftTail = leftWords.takeLast(size).joinToString(" ")
            val rightHead = rightWords.take(size).joinToString(" ")
            if (leftTail != rightHead) continue

            if (size >= 2 || leftWords.size <= 2 || right.startsWith("$leftTail ")) {
                return (leftWords + rightWords.drop(size)).joinToString(" ").trim()
            }
        }

        return "$left $right".trim()
    }
}
