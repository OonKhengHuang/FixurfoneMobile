package com.example.fixurfonemobile.database

import android.util.Log
import com.example.fixurfonemobile.model.Diagnose
import com.google.firebase.database.FirebaseDatabase

class DiagnoseDB {
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun updateDiagnose(diagnoseID: String, value:String){
        db.getReference("Diagnose").child(diagnoseID!!).child("status").setValue(value)
            .addOnFailureListener {
                Log.e("Firebase", "Error in loading the file: ${it.toString()}")

            }
    }
}