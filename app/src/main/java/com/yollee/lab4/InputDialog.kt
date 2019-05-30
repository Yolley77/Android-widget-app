package com.yollee.lab4

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.EditText
import android.content.SharedPreferences
import android.content.Context
import android.view.View
import android.app.DatePickerDialog.OnDateSetListener
import kotlinx.android.synthetic.main.activity_input_dialog.*
import android.app.*
import android.app.AlarmManager
import android.app.NotificationManager
import android.support.v4.app.NotificationCompat
import android.app.PendingIntent
import android.os.Build
import java.util.*


lateinit var resultValue: Intent
lateinit var sp: SharedPreferences
lateinit var date: EditText

//var notification : Notification? = null

val WIDGET_PREF = "widget_pref"
val WIDGET_DATE = "widget_date_"

class InputDialog : AppCompatActivity() {

    var DIALOG_DATE = 1
    var myYear = 2019
    var myMonth = 8
    var myDay = 23

    var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID

    var nm: NotificationManager? = null
    var am: AlarmManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_dialog)

        nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        am = getSystemService(ALARM_SERVICE) as AlarmManager?

        // извлекаем ID конфигурируемого виджета
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        // формируем intent ответа
        resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)

        // отрицательный ответ
        setResult(Activity.RESULT_CANCELED, resultValue)

        sp = getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE)
        selectDate.text = sp.getString(WIDGET_DATE + widgetID, "23/8/2019")
    }

    fun onSetClick(v: View) {

        // worked
        /*val intent = Intent(baseContext, DateWidget::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, "CHANNEL_ID")
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(pendingIntent)
            .setContentTitle("Уведомление")
            .setTicker("this is ticker text")
            .setSubText("Сабтекст уведомления")
            .setContentText("Здесь будет написана дата")
            .setVibrate(longArrayOf(1000, 1000))
            .setPriority(PRIORITY_HIGH)

        resultValue.putExtra("notification", notificationBuilder.build())
        notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= 19)
            am?.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent)
        else
            am?.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent)*/

        /*createChannelIfNeeded(nm!!)
        nm?.notify(101, notificationBuilder.build())*/

        val notification = getNotification(baseContext)
        val notificationIntent = Intent(baseContext, DateWidget::class.java)
        notificationIntent.putExtra("notif", notification)
        val pendingIntent = PendingIntent.getBroadcast(baseContext, 0, notificationIntent, 0)

        //здесь календарю задаётся дата и время, затем он кидается в аларм
        val notifyTime = Calendar.getInstance()
        notifyTime.set(Calendar.HOUR_OF_DAY, 9)
        notifyTime.set(Calendar.MINUTE, 0)
        notifyTime.set(Calendar.SECOND, 0)
        notifyTime.set(Calendar.YEAR, myYear)
        notifyTime.set(Calendar.MONTH, myMonth)
        notifyTime.set(Calendar.DAY_OF_MONTH, myDay)


        if (Build.VERSION.SDK_INT >= 19) am?.setExact(AlarmManager.RTC_WAKEUP, notifyTime.timeInMillis, pendingIntent)
        else am?.set(AlarmManager.RTC_WAKEUP, notifyTime.timeInMillis, pendingIntent)

        sp.edit().putString(WIDGET_DATE + widgetID, selectDate.text.toString()).apply()
        DateWidget.updateAppWidget(this, AppWidgetManager.getInstance(this), widgetID)
        setResult(Activity.RESULT_OK, resultValue)
        finish()

    }

    fun getNotification(context : Context) : Notification {
        val intent = Intent(context, DateWidget::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(context, "101")
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(pendingIntent)
            .setContentTitle("Уведомление")
            .setTicker("this is ticker text")
            .setSubText("Сабтекст уведомления")
            .setContentText("Дата - $myDay/$myMonth/$myYear")
            .setVibrate(longArrayOf(1000, 1000))
            .setPriority(Notification.PRIORITY_HIGH)

        return notificationBuilder.build()
    }

    fun onDialogClick(view: View) {
        showDialog(DIALOG_DATE)
    }


    override fun onCreateDialog(id: Int): Dialog {
        return if (id == DIALOG_DATE) {
            DatePickerDialog(this, myCallBack, myYear, myMonth, myDay)
        } else super.onCreateDialog(id)
    }

    var myCallBack: OnDateSetListener =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myYear = year
            myMonth = monthOfYear
            myDay = dayOfMonth
            selectDate.text = "$myDay/$myMonth/$myYear"
        }

}