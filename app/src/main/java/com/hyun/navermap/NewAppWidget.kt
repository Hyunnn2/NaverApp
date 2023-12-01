package com.hyun.navermap

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)

    // 위젯이 클릭되었을 때 BookMarkFragment를 시작할 수 있도록 Intent 설정
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("widget_clicked", true)  // 위젯 클릭을 식별하기 위한 플래그
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    // 위젯 클릭 이벤트 설정
    views.setOnClickPendingIntent(R.id.widget_text, pendingIntent)

    // 위젯 업데이트
    appWidgetManager.updateAppWidget(appWidgetId, views)
}