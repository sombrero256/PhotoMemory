package edu.michael.coe.photographicmemory

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.widget.Toast
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat

const val DATABASENAME = "PHOTOMEMDB"
const val REMINDERSTABLENAME = "Reminders"
const val COL_DATE = "date"
const val COL_TIME = "time"
const val COL_URI = "uri"
const val COL_TEXT = "text"
const val COL_NOTIF_ID = "notificationId"
const val COL_ID = "id"

class SQLHelper(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    private fun createTables(db:SQLiteDatabase?) {

        val createReminderTable = "CREATE TABLE " + REMINDERSTABLENAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
         COL_DATE + " VARCHAR(256)," + COL_TIME + " VARCHAR(256)," + COL_URI + " VARCHAR(256)," + COL_TEXT + " VARCHAR(256)," + COL_NOTIF_ID + " INT(255))"
        db?.execSQL(createReminderTable)
    }

    fun insertReminder(r: Reminder){
        val database = this.readableDatabase
        val cv = ContentValues()

        cv.put(COL_DATE, r.date.toString())
        cv.put(COL_TIME, r.time.toString())
        cv.put(COL_URI, r.imageURI.toString())
        cv.put(COL_TEXT, r.notificationText)
        cv.put(COL_NOTIF_ID, r.notificationId.toString())
        val result = database.insert(REMINDERSTABLENAME, null, cv)
        if(result == 0.toLong()){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getReminder(id:Int) : MutableList<Reminder> {
        val list: MutableList<Reminder> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $REMINDERSTABLENAME WHERE $COL_NOTIF_ID = $id"
        val result = db.rawQuery(query, null)
        val datesdf = SimpleDateFormat("yyyy-MM-dd")
        val timesdf = SimpleDateFormat("HH:mm:ss")
        if(result.moveToFirst()){
            val r = Reminder()
            val dString = result.getString(result.getColumnIndex(COL_DATE)).toString()
            val tString = result.getString(result.getColumnIndex(COL_TIME)).toString()
            val d = datesdf.parse(dString)
            val t = timesdf.parse(tString)
            r.date = Date(d.year, d.month, d.day)
            r.time = Time(t.hours, t.minutes, t.seconds)
            r.imageURI = Uri.parse(result.getString(result.getColumnIndex(COL_URI)))
            r.notificationText = result.getString(result.getColumnIndex(COL_TEXT))
            r.notificationId = result.getInt(result.getColumnIndex(COL_NOTIF_ID))
            list.add(r)
        }
        result.close()
        return list
    }

    fun getAllReminders() : MutableList<Reminder> {
        val list: MutableList<Reminder> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $REMINDERSTABLENAME"
        val result = db.rawQuery(query, null)
        val datesdf = SimpleDateFormat("yyyy-MM-dd")
        val timesdf = SimpleDateFormat("HH:mm:ss")
        if(result.moveToFirst()){
            val r = Reminder()
            val dString = result.getString(result.getColumnIndex(COL_DATE)).toString()
            val tString = result.getString(result.getColumnIndex(COL_TIME)).toString()
            val d = datesdf.parse(dString)
            val t = timesdf.parse(tString)
            r.date = Date(d.year, d.month, d.day)
            r.time = Time(t.hours, t.minutes, t.seconds)
            r.imageURI = Uri.parse(result.getString(result.getColumnIndex(COL_URI)))
            r.notificationText = result.getString(result.getColumnIndex(COL_TEXT))
            r.notificationId = result.getInt(result.getColumnIndex(COL_NOTIF_ID))
            list.add(r)
        }
        result.close()
        return list
    }

    fun deleteReminder(r:Reminder){
        val db = this.readableDatabase

        val del = "DELETE FROM " + REMINDERSTABLENAME + " WHERE " + COL_TEXT + "+" + " \'" + r.notificationText + "\'"
        db.execSQL(del)
    }

}