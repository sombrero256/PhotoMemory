package edu.michael.coe.photographicmemory

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat


class AddReminderActivity : AppCompatActivity() {
    val reqCode = 100;
    val permCode = 101;
    var createdReminder : Reminder? = null
    var selectedImageURI : Uri? = null
    var numOfReminders = 0
    var db : SQLHelper? = null
    val NOTIFICATION_ID = "notification-id"
    val NOTIFICATION = "notification"
    var sharedPref : SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addreminder)
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), permCode)

        }
        sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
        //check the preference. If it doesn't exist, make the preference
        if(sharedPref!!.getInt(getString(R.string.num_reminders_key), -1) == -1){
            with(sharedPref!!.edit()) {
                putInt(getString(R.string.num_reminders_key), 0)
                commit()
            }
        }
        numOfReminders = sharedPref!!.getInt(getString(R.string.num_reminders_key), -1)
        db = SQLHelper(this)
        createdReminder = Reminder()

    }

    fun onClickAddPicture(v: View){
        //select a picture
        //change pick intent to specifically go to gallery later.
        val pickIntent = Intent()
        pickIntent.setType("image/*")
        pickIntent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(pickIntent, "Select an image"), reqCode)

    }

    fun onClickFinishReminder(v: View){
        //Some hardcoded numbers to account for the way the functions work
        val d = findViewById<TextView>(R.id.day).text.toString().toInt()
        val m = findViewById<TextView>(R.id.month).text.toString().toInt() - 1
        val y = findViewById<TextView>(R.id.year).text.toString().toInt() - 1900
        val h = findViewById<TextView>(R.id.hour).text.toString().toInt()
        val min = findViewById<TextView>(R.id.minute).text.toString().toInt()


        //Deprecated functions are never actually removed from java
        createdReminder!!.date = Date(y, m, d)
        //Hard coding second because I don't care when during the minute the notification comes up
        createdReminder!!.time = Time(h, min, 0)
        createdReminder!!.notificationText = findViewById<TextView>(R.id.notificationMessageTextBox).text.toString()
        //copy pic to my external storage
        Log.d("time", createdReminder!!.date.toString() + createdReminder!!.time.toString())
        /*var original = File(selectedImageURI!!.toString())
        var dir = this.applicationContext.getDir(getString(R.string.imagesdir), MODE_PRIVATE)
        var copyLoc = File(dir.path + "/$numOfReminders")
        original.copyTo(copyLoc, true, 2056)*/
        scheduleNotification(buildNotification(createdReminder!!.notificationText!!), calculateDelay(createdReminder!!.date!!, createdReminder!!.time!!))
        var id = sharedPref!!.getInt(getString(R.string.num_reminders_key), -1)
        createdReminder!!.notificationId = id
        id += 1
        with(sharedPref!!.edit()){
            putInt(getString(R.string.num_reminders_key), id)
            commit()
        }
        //ADD TO DB
        db!!.insertReminder(createdReminder!!)
        //Add notification

        //Remove picture from gallery
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == reqCode && resultCode == RESULT_OK){
            var t = findViewById<ImageView>(R.id.imageView)
            t.setImageURI(data!!.data)
            selectedImageURI = data!!.data
            //remove the following line later
            createdReminder!!.imageURI = data!!.data
        }
        Toast.makeText(this, selectedImageURI!!.path, Toast.LENGTH_LONG).show()
    }

    fun scheduleNotification(n: Notification, d: Long){

        val notificationIntent = Intent(this, PhotoNotificationPublisher::class.java)
        notificationIntent.putExtra(NOTIFICATION_ID, sharedPref!!.getInt(getString(R.string.num_reminders_key), -1))
        notificationIntent.putExtra(NOTIFICATION, n)
        var p = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        var alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Toast.makeText(this, alarmMgr.toString(), Toast.LENGTH_LONG).show()
        //sets an alarm d time in the future, where pendingintent p will run
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, d, p)

    }
    //maybe add support for showing the bitmap in the notification later
    fun buildNotification(notificationText: String): Notification {
        val builder = NotificationCompat.Builder(this, getString(R.string.notifications_channel_id))
        with(builder){
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("Photographic Memory Notification")
            setContentText(notificationText)
            setChannelId(getString(R.string.notifications_channel_id))
        }
        return builder.build()
    }

    @SuppressLint("SimpleDateFormat")
    fun calculateDelay(d: Date, t: Time): Long{
        var delay : Long = 0
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var tempDate = sdf.parse("$d $t")
        delay = ( tempDate.time - System.currentTimeMillis())
        Log.d("time", delay.toString())
        return delay
    }
}