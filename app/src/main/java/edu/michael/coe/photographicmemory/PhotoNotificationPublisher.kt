package edu.michael.coe.photographicmemory

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import edu.michael.coe.photographicmemory.SQLHelper

const val ACTION_SNOOZE = "edu.michael.coe.photographicmemory.ACTION_SNOOZE"
const val ACTION_NOTIFICATION = "edu.michael.coe.photographicmemory.ACTION_NOTIFICATION"
val NOTIFICATION_ID = "notification-id"
val NOTIFICATION = "notification"
class PhotoNotificationPublisher : BroadcastReceiver(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Receive", "Intent received")

        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification? = intent!!.getParcelableExtra(NOTIFICATION)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(context.getString(R.string.notifications_channel_id), "NOTIFICATION_CHANNEL_NAME", importance)
            assert(notificationManager != null)
            notificationManager!!.createNotificationChannel(notificationChannel)
        val id = intent!!.getIntExtra(NOTIFICATION_ID, 0)
        assert(notificationManager != null)
        notificationManager!!.notify(id, notification)
    }
}

class SnoozeReceive : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val db = SQLHelper(context!!)
        val r = db.getReminder(intent!!.extras?.get(NOTIFICATION_ID).toString().toInt())
        Toast.makeText(context, r[0].notificationId.toString(), Toast.LENGTH_LONG).show()
    }

}