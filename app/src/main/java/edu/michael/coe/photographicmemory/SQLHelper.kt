package edu.michael.coe.photographicmemory

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

const val DATABASENAME = "PHOTOMEMDB"
const val REMINDERSTABLENAME = "Reminders"
const val COL_DATE = "date"
const val COL_TIME = "time"
const val COL_URI = "uri"
const val COL_TEXT = "text"
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
         COL_DATE + " VARCHAR(256)," + COL_TIME + " VARCHAR(256)," + COL_URI + " VARCHAR(256)," + COL_TEXT + " VARCHAR(256))"
        db?.execSQL(createReminderTable)
    }

    fun insertReminder(r: Reminder){
        val database = this.readableDatabase
        val cv = ContentValues()

        cv.put(COL_DATE, r.date.toString())
        cv.put(COL_TIME, r.time.toString())
        cv.put(COL_URI, r.imageURI.toString())
        cv.put(COL_TEXT, r.notificationText)
        val result = database.insert(REMINDERSTABLENAME, null, cv)
        if(result == 0.toLong()){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteReminder(r:Reminder){
        val db = this.readableDatabase

        val del = "DELETE FROM " + REMINDERSTABLENAME + " WHERE " + COL_TEXT + "+" + " \'" + r.notificationText + "\'"
        db.execSQL(del)
    }

}