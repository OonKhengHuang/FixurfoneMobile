package com.example.fixurfonemobile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase

class ReservationItemViewModelFactory (private val db: FirebaseDatabase, private val application: Application, private val custID:String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ReservationItemViewModel::class.java)){
            return ReservationItemViewModel(db, application, custID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}