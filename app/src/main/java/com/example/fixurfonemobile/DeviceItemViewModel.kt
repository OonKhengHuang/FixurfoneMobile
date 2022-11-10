package com.example.fixurfonemobile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fixurfonemobile.model.Device
import com.google.firebase.database.*

class DeviceItemViewModel(database: FirebaseDatabase, app: Application, private val custID: String): ViewModel() {
    private val db = database
    private val _navigateToItemDetails = MutableLiveData<String?>()
    val navigateToItemDetails
        get() = _navigateToItemDetails
    init {
        getData()
    }
    var mutableData = MutableLiveData<List<Device>>()
    val containResult = MutableLiveData<Boolean>()
    fun onItemClicked(deviceID: String?){
        _navigateToItemDetails.value = deviceID
    }
    fun onItemNavigated() {
        _navigateToItemDetails.value = null
    }

    private fun getData() {
        val deviceList: MutableList<Device> = mutableListOf()
        var dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Device")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(v in snapshot.children)
                    {
                        var deviceData = v.getValue(Device::class.java)
                        if(deviceData?.custID == custID){
                            deviceList.add(deviceData!!)
                            mutableData.value = deviceList
                        }
                    }
                    containResult.value = true

                }else{

                    containResult.value = false
                }
                if(deviceList.size <= 0)
                {
                    containResult.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
}