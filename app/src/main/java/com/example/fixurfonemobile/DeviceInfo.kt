package com.example.fixurfonemobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.fixurfonemobile.database.DeviceDB
import com.example.fixurfonemobile.databinding.FragmentDeviceInfoBinding
import com.example.fixurfonemobile.model.*
import com.google.firebase.database.*
import com.jaredrummler.android.device.DeviceName


class DeviceInfo : Fragment() {
    private lateinit var custID:String
    private lateinit var binding: FragmentDeviceInfoBinding
    private lateinit var db:FirebaseDatabase
    private lateinit var deviceN:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_device_info, container, false )
        val sharedPrefFile = "loginSharedPreferences"
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        custID = sharedPreferences.getString("userUID", null).toString()

        //logout id unauthorized user login
        if(custID == null){
            val intent = Intent(activity,Login::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this.requireActivity())
        }

        DeviceName.init(this.requireContext())
        deviceN = DeviceName.getDeviceName()
        binding.deviceName.text = deviceN
        binding.Pid.text = Build.ID
        binding.product.text = Build.PRODUCT
        binding.brand.text = Build.BRAND
        binding.device.text = Build.DEVICE
        binding.board.text = Build.BOARD
        binding.bootloader.text = Build.BOOTLOADER
        binding.display.text = Build.DISPLAY
        binding.hardware.text = Build.HARDWARE
        binding.manufacturer.text = Build.MANUFACTURER
        binding.model.text = Build.MODEL
        binding.sdk.text = Build.VERSION.SDK

        db = FirebaseDatabase.getInstance()
//        db.getReference("Device").child(Build.ID!!).get().addOnSuccessListener {
//            val deviceFound = it.getValue(Device::class.java)
//            if(deviceFound != null)
//            {
//                binding.register.visibility = View.GONE
//            }
//
//        }.addOnFailureListener {
//            //operation upon the failure condition
//            Log.e("Firebase", "Error in loading the file: ${it.toString()}")
//        }
        var dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Device")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(v in snapshot.children)
                    {
                        var deviceData = v.getValue(Device::class.java)
                        if(deviceData?.deviceID == Build.ID)
                        {
                            binding.register.visibility = View.GONE
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        binding.register.setOnClickListener {

            var device = Device(Build.ID, custID, Build.PRODUCT,Build.BRAND, Build.DEVICE,deviceN, Build.BOARD,Build.BOOTLOADER,
                Build.DISPLAY,Build.HARDWARE,Build.MANUFACTURER,Build.MODEL,Build.VERSION.SDK)
            var deviceDB = DeviceDB()
            deviceDB.addDevice(device)
            Toast.makeText(this.requireActivity(),"Device has been registered successfully", Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }

}