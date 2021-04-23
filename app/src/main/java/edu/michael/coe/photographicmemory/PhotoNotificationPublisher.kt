package edu.michael.coe.photographicmemory

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi


class PhotoNotificationPublisher : BroadcastReceiver(){
    val NOTIFICATION_ID = "notification-id"
    val NOTIFICATION = "notification"
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