package edu.michael.coe.photographicmemory

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ViewReminderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewreminder)
        val db = SQLHelper(this)
        var ca = ReminderListAdapter(this, db.getAllReminders() as ArrayList<Reminder>)
        //TODO fix this crash when the array is empty
        findViewById<ListView>(R.id.list).adapter = ca
    }
}