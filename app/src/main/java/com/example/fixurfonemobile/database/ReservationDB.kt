package com.example.fixurfonemobile.database

import android.util.Log
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.FirebaseDatabase

class ReservationDB {
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun addReservation(reserve: Reservation){
        val reserveID = reserve.reservationID
        db.getReference("Reservation").child(reserveID!!).setValue(reserve)
            .addOnFailureListener {
            Log.e("Firebase", "Error in loading the file: ${it.toString()}")

        }
    }

    fun deleteReservation(reserveID: String){
        db.getReference("Reservation").child(reserveID!!).removeValue()
            .addOnFailureListener {
                Log.e("Firebase", "Error in loading the file: ${it.toString()}")

            }
    }
}