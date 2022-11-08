package com.example.fixurfonemobile.model

import java.time.LocalDateTime
import java.util.*

data class Reservation (
    var reservationID: String? = "",
    var custID: String? = "",
    var recordID: String? = "",
    var date: String? = "",
    var bookingDate: String? = "",
    var bookingTime: String? = "",
    var serviceType: List<String>? = emptyList(),
    var phoneModel: String? = "",
    var remark: String? = "",
    var status: String? = "",
    var repairStatus: String? = ""
)