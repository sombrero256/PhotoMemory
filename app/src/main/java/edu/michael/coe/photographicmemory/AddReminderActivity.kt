package edu.michael.coe.photographicmemory

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
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

        //var original = File(createdReminder!!.imageURI!!.path)
        //var dir = this.applicationContext.getDir(getString(R.string.imagesdir), MODE_PRIVATE)
        //var copyLoc = File(dir.path + "/$numOfReminders")
        //original.copyTo(copyLoc, true, 2056)

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

            //remove the following line later
            createdReminder!!.imageURI = data!!.data
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun scheduleNotification(n: Notification, d: Long){

        val notificationIntent = Intent(this, PhotoNotificationPublisher::class.java)
        notificationIntent.putExtra(NOTIFICATION_ID, sharedPref!!.getInt(getString(R.string.num_reminders_key), -1))
        notificationIntent.putExtra(NOTIFICATION, n)
        var p = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        var alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Toast.makeText(this, alarmMgr.toString(), Toast.LENGTH_LONG).show()
        //sets an alarm d for time, where pendingintent p will run
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, d, p)

    }
    //maybe add support for showing the bitmap in the notification later
    //A couple of intents to add actions to our notification

    fun buildNotification(notificationText: String): Notification {
        val snoozeIntent = Intent(this, SnoozeReceive::class.java)
        snoozeIntent.putExtra(NOTIFICATION_ID, sharedPref!!.getInt(getString(R.string.num_reminders_key), -1))

        //TODO make sure that the flag is correct
        val snoozePendingIntent = PendingIntent.getBroadcast(this, reqCode, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, getString(R.string.notifications_channel_id))
        with(builder){
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("Photographic Memory Notification")
            val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), createdReminder!!.imageURI)
            setLargeIcon(bitmap)
            addAction(R.mipmap.ic_launcher_round, "Snooze", snoozePendingIntent)
            setContentText(notificationText)
            setChannelId(getString(R.string.notifications_channel_id))
        }
        return builder.build()
    }

    @SuppressLint("SimpleDateFormat")
    fun calculateDelay(d: Date, t: Time): Long{
        var delay : Long = 0
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var tempDate = sdf.parse(d.toString() + " " + t.toString())
        delay = tempDate!!.time
        Log.d("time", d.toString() + " " + t.toString())
        Log.d("time", delay.toString())

        return delay
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        var thePath = "no-path-found"
        val filePathColumn = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        if (cursor!!.moveToFirst()) {
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            thePath = cursor.getString(columnIndex)
        }
        cursor.close()
        //TODO find the directory that pictures are in
        return "/picturedirectorylol/$thePath"
    }


}