package edu.michael.coe.photographicmemory

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class PhotoNotificationPublisher : BroadcastReceiver(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Receive", "Intent received")

        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification? = intent!!.getParcelableExtra(NOTIFICATION)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(context.getString(R.string.notifications_channel_id), "NOTIFICATION_CHANNEL_NAME", importance)
            notificationManager.createNotificationChannel(notificationChannel)
        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(id, notification)
    }
}

class SnoozeReceive : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val db = SQLHelper(context!!)
        val r = db.getReminder(intent!!.extras?.get(NOTIFICATION_ID).toString().toInt())
        val notificationMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationMgr.cancel(r[0].notificationId)
        Toast.makeText(context, r[0].notificationId.toString(), Toast.LENGTH_LONG).show()
        scheduleNotification(context, buildNotification(context, r[0].notificationText /*r[0].imageURI*/), 86400000 + System.currentTimeMillis())
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun scheduleNotification(context: Context, n : Notification, d: Long){
        val sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE)

        val notificationIntent = Intent(context, PhotoNotificationPublisher::class.java)
        notificationIntent.putExtra(NOTIFICATION_ID, sharedPref!!.getInt(context.getString(R.string.num_reminders_key), -1))
        notificationIntent.putExtra(NOTIFICATION, n)
        val p = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Toast.makeText(context, alarmMgr.toString(), Toast.LENGTH_LONG).show()
        //sets an alarm d for time, where pendingintent p will run
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, d, p)

    }
    private fun buildNotification(context: Context, notificationText: String? /*uri : Uri?*/): Notification {
        val snoozeIntent = Intent(context, SnoozeReceive::class.java)
        val sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE)

        snoozeIntent.putExtra(NOTIFICATION_ID, sharedPref!!.getInt(context.getString(R.string.num_reminders_key), -1))

        //TODO make sure that the flag is correct

        val builder = NotificationCompat.Builder(context, context.getString(R.string.notifications_channel_id))
        with(builder){
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("Photographic Memory Notification")
            //val bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
            //setLargeIcon(bitmap)
            //no snoozing a second time you lazy bastards
            //addAction(R.mipmap.ic_launcher_round, "Snooze", snoozePendingIntent)

            setContentText(notificationText)
            setChannelId(context.getString(R.string.notifications_channel_id))
        }
        return builder.build()
    }

}

