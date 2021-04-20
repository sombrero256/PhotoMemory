package edu.michael.coe.photographicmemory

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.sql.Date
import java.sql.Time

class AddReminderActivity : AppCompatActivity() {
    val reqCode = 100;
    val permCode = 101;
    var createdReminder : Reminder? = null

    var db : SQLHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addreminder)
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), permCode)

        }
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
        //ADD TO DB
        db!!.insertReminder(createdReminder!!)
        //Remove picture from gallery
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == reqCode && resultCode == RESULT_OK){
            var t = findViewById<ImageView>(R.id.imageView)
            t.setImageURI(data!!.data)
            createdReminder!!.imageURI = data!!.data
        }
    }




}