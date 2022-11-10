package com.example.fixurfonemobile.model

import java.text.SimpleDateFormat
import java.util.*

class GetTodayDate {
    fun getTodayDate ():String{
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val currentDate = parser.format(Date())
        return currentDate
    }
}