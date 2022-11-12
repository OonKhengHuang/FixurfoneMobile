package com.example.fixurfonemobile.model

import java.io.Serializable

data class Customer (
    var custID: String? = null,
    var name: String? = null,
    var phoneNum: String? = null,
    var address: String? = null,
    var email: String? = null,
    var password: String? = null,
    var status: Int? = 0,
    var profilePic: String? = null,
    var salt: String? = null

)
