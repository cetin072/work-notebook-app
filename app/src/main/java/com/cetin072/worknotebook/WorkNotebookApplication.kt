package com.cetin072.worknotebook

import android.app.Application
import com.cetin072.worknotebook.data.WorkItemRepository
import com.cetin072.worknotebook.data.WorkNotebookDatabase

class WorkNotebookApplication : Application() {
    val database: WorkNotebookDatabase by lazy { WorkNotebookDatabase.getInstance(this) }
    val repository: WorkItemRepository by lazy { WorkItemRepository(database.workItemDao()) }
}
