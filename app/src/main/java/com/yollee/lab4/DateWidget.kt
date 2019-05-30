package com.yollee.lab4

import android.app.*
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.widget.RemoteViews
import android.content.Intent
import android.os.Build
import android.app.NotificationManager
import android.content.Context.ALARM_SERVICE
import android.widget.Toast

/**
 * Implementation of App Widget functionality.
 */

var notification : Notification? = null

class DateWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
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

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        val editor = context?.getSharedPreferences(
            WIDGET_PREF, Context.MODE_PRIVATE
        )?.edit()
        for (widgetID in appWidgetIds!!) {
            editor?.remove(WIDGET_DATE + widgetID)
        }
        editor?.apply()
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Читаем инфу из ИнпатДиалога
            val sp = context.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE)
            val date = sp.getString(WIDGET_DATE + appWidgetId, null) ?: return

            // Помещаем данные в текстовое поле
            val widgetView = RemoteViews(context.packageName, R.layout.date_widget)
            widgetView.setTextViewText(R.id.date, date)

            // Переход к ИнпатДиалогу
            val inputDialogIntent = Intent(context, InputDialog::class.java)
            inputDialogIntent.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            inputDialogIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pIntent = PendingIntent.getActivity(
                context, appWidgetId,
                inputDialogIntent, 0
            )
            widgetView.setOnClickPendingIntent(R.id.setDate, pIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, widgetView)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val nm = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(intent!!.hasExtra("notif")) {
            val notification : Notification = intent.getParcelableExtra("notif")
            createChannelIfNeeded(nm)
            nm.notify(101, notification)
            Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show()
        }


    }

    fun createChannelIfNeeded(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("101", "CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(notificationChannel)
        }
    }
}
