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
}
