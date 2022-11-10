package com.example.fixurfonemobile

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.fixurfonemobile.database.DeviceDB
import com.example.fixurfonemobile.database.ReservationDB
import com.example.fixurfonemobile.databinding.ActivityDeviceDetailsBinding
import com.example.fixurfonemobile.model.DateTimeParser
import com.example.fixurfonemobile.model.Device
import com.example.fixurfonemobile.model.GetTodayDate
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.*
import com.jaredrummler.android.device.DeviceName

class DeviceDetails : AppCompatActivity() {
    private lateinit var custID:String
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: ActivityDeviceDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_device_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val deviceID = intent.getStringExtra("deviceID")
        db = FirebaseDatabase.getInstance()
        db.getReference("Device").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(v in snapshot.children)
                    {
                        var reserveData = v.getValue(Device::class.java)
                        if(reserveData?.deviceID == deviceID){

                            binding.deviceName.text = reserveData?.deviceName
                            binding.Pid.text = reserveData?.deviceID
                            binding.product.text = reserveData?.product
                            binding.brand.text = reserveData?.brand
                            binding.device.text = reserveData?.device
                            binding.board.text = reserveData?.board
                            binding.bootloader.text = reserveData?.bootloader
                            binding.display.text = reserveData?.display
                            binding.hardware.text = reserveData?.hardware
                            binding.manufacturer.text = reserveData?.manufacturer
                            binding.model.text = reserveData?.model
                            binding.sdk.text = reserveData?.sdk


                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.back.setOnClickListener {
            val intent =
                Intent(this, ViewDeviceList::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.remove.setOnClickListener{
            val view = View.inflate(this, R.layout.delete_device_dialog, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            var btnCancel: Button = view.findViewById(R.id.btn_cancel)
            var btnOkay: Button = view.findViewById(R.id.btn_okay)
            btnCancel.setOnClickListener{
                dialog.dismiss()
            }

            btnOkay.setOnClickListener{
                var deviceDB = DeviceDB()
                deviceDB.deleteDevice(deviceID!!)
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

            }

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}