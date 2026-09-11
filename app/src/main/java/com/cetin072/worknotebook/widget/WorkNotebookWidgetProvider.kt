package com.cetin072.worknotebook.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.cetin072.worknotebook.MainActivity
import com.cetin072.worknotebook.R
import com.cetin072.worknotebook.data.WorkNotebookDatabase
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkNotebookWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        refreshWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        requestUpdate(context)
    }

    private fun refreshWidgets(
        context: Context,
        manager: AppWidgetManager,
        ids: IntArray,
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val today = LocalDate.now().toString()
                val items = WorkNotebookDatabase.getInstance(context)
                    .workItemDao()
                    .getTodayPending(today)

                ids.forEach { widgetId ->
                    manager.updateAppWidget(widgetId, buildViews(context, items))
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun buildViews(context: Context, items: List<com.cetin072.worknotebook.data.WorkItemEntity>): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_work_notebook)
        val visibleItems = items.take(3)
        val rowIds = intArrayOf(R.id.widget_item_1, R.id.widget_item_2, R.id.widget_item_3)

        rowIds.forEachIndexed { index, viewId ->
            val item = visibleItems.getOrNull(index)
            if (item == null) {
                views.setViewVisibility(viewId, View.GONE)
            } else {
                val prefix = item.workTime?.let { "$it  " }.orEmpty()
                views.setTextViewText(viewId, "${index + 1}. $prefix${item.title}")
                views.setViewVisibility(viewId, View.VISIBLE)
            }
        }

        val moreCount = (items.size - visibleItems.size).coerceAtLeast(0)
        views.setTextViewText(
            R.id.widget_more,
            if (moreCount > 0) "+ ${moreCount}개 더 있음" else "오늘 미완료 ${items.size}건",
        )

        val openIntent = Intent(context, MainActivity::class.java)
        val openPending = PendingIntent.getActivity(
            context,
            100,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val recordIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_WIDGET_ACTION, ACTION_RECORD)
        }
        val recordPending = PendingIntent.getActivity(
            context,
            101,
            recordIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val cameraIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_WIDGET_ACTION, ACTION_CAMERA)
        }
        val cameraPending = PendingIntent.getActivity(
            context,
            102,
            cameraIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        views.setOnClickPendingIntent(R.id.widget_root, openPending)
        views.setOnClickPendingIntent(R.id.widget_record, recordPending)
        views.setOnClickPendingIntent(R.id.widget_camera, cameraPending)
        return views
    }

    companion object {
        const val EXTRA_WIDGET_ACTION = "widget_action"
        const val ACTION_RECORD = "record"
        const val ACTION_CAMERA = "camera"

        fun requestUpdate(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, WorkNotebookWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isEmpty()) return

            val intent = Intent(context, WorkNotebookWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(intent)
        }
    }
}
