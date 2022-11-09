package com.example.fixurfonemobile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.*


class ReservationItemViewModel (database: FirebaseDatabase, app: Application, private val custID: String): ViewModel() {
    private val db = database
    private val _navigateToItemDetails = MutableLiveData<String?>()
    val navigateToItemDetails
        get() = _navigateToItemDetails
    init {
        getData()
    }
    var mutableData = MutableLiveData<List<Reservation>>()
    val containResult = MutableLiveData<Boolean>()
    fun onItemClicked(reservationID: String?){
        _navigateToItemDetails.value = reservationID
    }
    fun onItemNavigated() {
        _navigateToItemDetails.value = null
    }

    private fun getData() {
        val reserveList: MutableList<Reservation> = mutableListOf()
        var dbRef:DatabaseReference = FirebaseDatabase.getInstance().getReference("Reservation")
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(v in snapshot.children)
                    {
                        var reserveData = v.getValue(Reservation::class.java)
                        if(reserveData?.custID == custID){
                            reserveList.add(reserveData!!)
                            mutableData.value = reserveList
                        }
                    }
                    containResult.value = true

                }else{

                    containResult.value = false
                }
                if(reserveList.size <= 0)
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