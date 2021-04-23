package edu.michael.coe.photographicmemory

import android.net.Uri
import java.sql.Date
import java.sql.Time


class Reminder {
    var date : Date? = null
    var time: Time? = null
    var imageURI : Uri? = null
    var notificationText : String? = null
    var notificationId : Int = -1

    constructor(){
    }
}