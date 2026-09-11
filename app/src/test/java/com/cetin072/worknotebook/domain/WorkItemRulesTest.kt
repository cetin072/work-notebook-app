package com.cetin072.worknotebook.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkItemRulesTest {
    @Test
    fun `explicit title wins and is normalized`() {
        val title = WorkItemRules.makeTitle("  대표   보고  ", "긴 업무 내용")
        assertEquals("대표 보고", title)
    }

    @Test
    fun `blank title falls back to content`() {
        val title = WorkItemRules.makeTitle("", "  견적서   확인 후 보고  ")
        assertEquals("견적서 확인 후 보고", title)
    }

    @Test
    fun `generated title is capped`() {
        val title = WorkItemRules.makeTitle("", "가".repeat(80))
        assertEquals(60, title.length)
        assertTrue(title.endsWith("…"))
    }

    @Test
    fun `today section only contains incomplete matching date`() {
        assertTrue(WorkItemRules.isTodayPending("2026-09-11", false, "2026-09-11"))
        assertFalse(WorkItemRules.isTodayPending("2026-09-11", true, "2026-09-11"))
        assertFalse(WorkItemRules.isTodayPending("2026-09-12", false, "2026-09-11"))
        assertFalse(WorkItemRules.isTodayPending(null, false, "2026-09-11"))
    }

    @Test
    fun `explicit work markers split multiple tasks`() {
        assertEquals(
            listOf("대표님 보고자료 정리", "범한 견적 확인", "농장 일정 확인"),
            WorkItemRules.splitWorkSegments(
                "대표님 보고자료 정리 다음 업무 범한 견적 확인 다음 건 농장 일정 확인",
            ),
        )
    }

    @Test
    fun `geudaeum marker also splits tasks`() {
        assertEquals(
            listOf("계약서 확인", "세금계산서 발행"),
            WorkItemRules.splitWorkSegments("계약서 확인 그다음 세금계산서 발행"),
        )
    }

    @Test
    fun `plain word next does not split normal sentence`() {
        val content = "다음 주 월요일 대표님께 보고한다"
        assertEquals(listOf(content), WorkItemRules.splitWorkSegments(content))
    }

    @Test
    fun `empty fragments around markers are ignored`() {
        assertEquals(
            listOf("첫 업무", "둘째 업무"),
            WorkItemRules.splitWorkSegments("다음 업무 첫 업무 다음 업무 다음 건 둘째 업무"),
        )
    }
}
