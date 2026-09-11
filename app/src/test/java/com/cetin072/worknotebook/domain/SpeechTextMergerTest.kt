package com.cetin072.worknotebook.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class SpeechTextMergerTest {
    @Test
    fun `same transcript is not duplicated`() {
        assertEquals("산업단지공단 가기", SpeechTextMerger.merge("산업단지공단 가기", "산업단지공단 가기"))
    }

    @Test
    fun `cumulative recognition result replaces shorter base`() {
        assertEquals(
            "대표님께 견적 검토 결과 보고",
            SpeechTextMerger.merge("대표님께 견적", "대표님께 견적 검토 결과 보고"),
        )
    }

    @Test
    fun `overlapping fragments are merged once`() {
        assertEquals(
            "대표님께 견적 검토 결과 보고",
            SpeechTextMerger.merge("대표님께 견적 검토", "견적 검토 결과 보고"),
        )
    }

    @Test
    fun `unrelated fragments are appended`() {
        assertEquals("범한 미팅 자료 준비 오후 세시 방문", SpeechTextMerger.merge("범한 미팅 자료 준비", "오후 세시 방문"))
    }
}
