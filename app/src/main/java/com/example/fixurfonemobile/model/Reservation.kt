package com.example.fixurfonemobile.model

import java.util.*

data class Reservation (
    var reservationID: String = "",
    var custID: String = "",
    var recordID: String = "",
    var date: Date = Date(),
    var bookingDate: Date = Date(),
    var bookingTime: String = "",
    var serviceType: Array<String>,
    var phoneModel: String = "",
    var remark: String = "",
    var status: String = "",
    var repairStatus: String = ""
)