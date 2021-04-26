package edu.michael.coe.photographicmemory

import android.content.Context
import android.database.DataSetObserver
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView

class ReminderListAdapter : ListAdapter {

var arrayList: ArrayList<Reminder>
var context: Context

constructor(c : Context, a : ArrayList<Reminder>){
    context = c
    arrayList = a
}

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
        var r = arrayList.get(position)
        var cv = convertView
        if (convertView == null) {
            var layoutInflater = LayoutInflater.from(context);
            cv = layoutInflater.inflate(R.layout.list_row, null);
            cv.setOnClickListener(View.OnClickListener() {
                fun onClick(v: View) {
                }
            });
            var Text = cv.findViewById<TextView>(R.id.list_rowText)
            var Img = cv.findViewById<ImageView>(R.id.list_rowImage)

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