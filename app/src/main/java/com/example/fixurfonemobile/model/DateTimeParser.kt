package com.example.fixurfonemobile.model

import java.text.SimpleDateFormat

class DateTimeParser {
    fun customParse(date:String):String
    {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = SimpleDateFormat("dd/M/yyyy")
        val output = formatter.format(parser.parse(date))

        return output
    }
}