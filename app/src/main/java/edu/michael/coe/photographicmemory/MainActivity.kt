package edu.michael.coe.photographicmemory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickAddReminderButton(v: View){
        val intent = Intent(this, AddReminderActivity::class.java)
        startActivity(intent)
    }
    fun onClickRemoveReminderButton(v: View){
        val intent = Intent(this, RemoveReminderActivity::class.java)
        startActivity(intent)
    }
    fun onClickViewReminderButton(v: View){
        val intent = Intent(this, ViewReminderActivity::class.java)
        startActivity(intent)
    }

}