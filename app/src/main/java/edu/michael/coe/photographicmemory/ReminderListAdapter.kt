package edu.michael.coe.photographicmemory

import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class ReminderListAdapter(c: Context, a: ArrayList<Reminder>) : ListAdapter {

    var arrayList: ArrayList<Reminder> = a
    var context: Context = c

    override fun registerDataSetObserver(observer: DataSetObserver?) {
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
    }

    override fun getCount(): Int {
        return arrayList.count()
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
            return false
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val r = arrayList[position]
        var cv = convertView

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context);
            cv = layoutInflater.inflate(R.layout.list_row, null);
            cv.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    val db = SQLHelper(context)
                    val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
                    alertDialog.setTitle("Remove notification?")
                    alertDialog.setMessage("The alert will not go off, an associated data will be removed.")

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Delte", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            val notificationMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            var id = v.findViewById<TextView>(R.id.idText).text.toString().toInt()
                            db.deleteReminderByNotificationId(id)
                            notificationMgr.cancel(id)
                            parent!!.invalidate()

                        }
                    })
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Don'te", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                        }
                    })
                    alertDialog.show()
                }
            })
            var Text = cv.findViewById<TextView>(R.id.list_rowText)
            var Img = cv.findViewById<ImageView>(R.id.list_rowImage)
            var IdBox = cv.findViewById<TextView>(R.id.idText)
            IdBox.text = r.notificationId.toString()
            Text.text = r.notificationText

            //Img.setImageURI(r.imageURI)
        }
        return cv!!
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return arrayList.size
    }

    override fun isEmpty(): Boolean {
        return arrayList.isEmpty()
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return true
    }

}