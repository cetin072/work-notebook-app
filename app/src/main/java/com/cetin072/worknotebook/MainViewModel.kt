package com.cetin072.worknotebook

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cetin072.worknotebook.data.DraftStore
import com.cetin072.worknotebook.data.WorkDraft
import com.cetin072.worknotebook.data.WorkItemEntity
import com.cetin072.worknotebook.domain.WorkItemRules
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as WorkNotebookApplication
    private val repository = app.repository
    private val draftStore = DraftStore(application)

    val items: StateFlow<List<WorkItemEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayItems: StateFlow<List<WorkItemEntity>> = items
        .map { list ->
            val today = LocalDate.now().toString()
            list.filter { WorkItemRules.isTodayPending(it.workDate, it.isCompleted, today) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _draft = MutableStateFlow(draftStore.load())
    val draft: StateFlow<WorkDraft> = _draft.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun setTitle(value: String) = updateDraft { copy(title = value) }
    fun setContent(value: String) = updateDraft { copy(content = value) }
    fun setWorkDate(value: String?) = updateDraft { copy(workDate = value) }
    fun setWorkTime(value: String?) = updateDraft { copy(workTime = value) }

    fun edit(item: WorkItemEntity) {
        val next = WorkDraft(
            title = item.title,
            content = item.content,
            workDate = item.workDate,
            workTime = item.workTime,
            editingId = item.id,
            editingCreatedAt = item.createdAt,
        )
        _draft.value = next
        draftStore.save(next)
        _message.value = "수정할 내용을 불러왔습니다."
    }

    fun cancelEditing() {
        clearDraft()
        _message.value = "수정을 취소했습니다."
    }

    fun save() {
        val current = _draft.value
        val content = current.content.trim()
        if (content.isBlank()) {
            _message.value = "업무 내용을 입력하세요."
            return
        }

        val now = System.currentTimeMillis()
        val item = WorkItemEntity(
            id = current.editingId ?: UUID.randomUUID().toString(),
            title = WorkItemRules.makeTitle(current.title, content),
            content = content,
            workDate = current.workDate,
            workTime = current.workTime,
            isCompleted = false,
            createdAt = current.editingCreatedAt ?: now,
            updatedAt = now,
        )

        viewModelScope.launch {
            repository.upsert(item)
            val wasEditing = current.editingId != null
            clearDraft()
            _message.value = if (wasEditing) "수정했습니다." else "저장했습니다."
        }
    }

    fun toggleCompleted(item: WorkItemEntity) {
        viewModelScope.launch {
            repository.setCompleted(item.id, !item.isCompleted)
            _message.value = if (item.isCompleted) "미완료로 되돌렸습니다." else "완료했습니다."
        }
    }

    fun clearDraft() {
        draftStore.clear()
        _draft.value = WorkDraft()
    }

    private fun updateDraft(block: WorkDraft.() -> WorkDraft) {
        val next = _draft.value.block()
        _draft.value = next
        draftStore.save(next)
        _message.value = null
    }
}
