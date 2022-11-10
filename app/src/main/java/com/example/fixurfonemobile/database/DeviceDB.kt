package com.example.fixurfonemobile.database

import android.util.Log
import com.example.fixurfonemobile.model.Device
import com.google.firebase.database.FirebaseDatabase

class DeviceDB {
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun addDevice(device: Device){
        val deviceID = device.deviceID
        db.getReference().child("Device").push().setValue(device)
            .addOnFailureListener {
                Log.e("Firebase", "Error in loading the file: ${it.toString()}")

            }
    }

    fun deleteDevice(deviceID: String){
        db.getReference("Devicce").child(deviceID!!).removeValue()
            .addOnFailureListener {
                Log.e("Firebase", "Error in loading the file: ${it.toString()}")

            }
    }
}