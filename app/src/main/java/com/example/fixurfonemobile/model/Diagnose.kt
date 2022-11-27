package com.example.fixurfonemobile.model

data class Diagnose (
    var reservationID: String? = "",
    var custID: String? = "",
    var diagnoseID: String? = "",
    var date: String? = "",
    var componentID: List<String>? = emptyList(),
    var phoneModel: String? = "",
    var remark: String? = "",
    var status: String? = "",
    var totalPrice: Int? = 0
)