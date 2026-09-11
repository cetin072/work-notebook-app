package com.cetin072.worknotebook.data

import android.content.Context

private const val PREFS_NAME = "work_notebook_draft"

data class WorkDraft(
    val title: String = "",
    val content: String = "",
    val workDate: String? = null,
    val workTime: String? = null,
    val editingId: String? = null,
    val editingCreatedAt: Long? = null,
)

class DraftStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): WorkDraft = WorkDraft(
        title = prefs.getString("title", "").orEmpty(),
        content = prefs.getString("content", "").orEmpty(),
        workDate = prefs.getString("workDate", null),
        workTime = prefs.getString("workTime", null),
        editingId = prefs.getString("editingId", null),
        editingCreatedAt = if (prefs.contains("editingCreatedAt")) {
            prefs.getLong("editingCreatedAt", 0L)
        } else {
            null
        },
    )

    fun save(draft: WorkDraft) {
        prefs.edit().apply {
            putString("title", draft.title)
            putString("content", draft.content)
            putString("workDate", draft.workDate)
            putString("workTime", draft.workTime)
            putString("editingId", draft.editingId)
            if (draft.editingCreatedAt != null) {
                putLong("editingCreatedAt", draft.editingCreatedAt)
            } else {
                remove("editingCreatedAt")
            }
        }.apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
