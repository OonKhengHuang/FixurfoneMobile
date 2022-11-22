package com.example.fixurfonemobile.database


import android.util.Log
import com.example.fixurfonemobile.model.Customer
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.FirebaseDatabase


class CustomerDB {
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun addCustomer(cust: Customer){
        val custID = cust.custID
        db.getReference("Customer").child(custID!!).setValue(cust)
            .addOnFailureListener {
                Log.e("Firebase", "Error in loading the file: ${it.toString()}")

            }
    }


}