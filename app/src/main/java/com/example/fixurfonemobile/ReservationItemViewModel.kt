package com.example.fixurfonemobile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.*


class ReservationItemViewModel (database: FirebaseDatabase, app: Application, private val reservationID: String): ViewModel() {
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
//        val listRef = db.getReference().
//            .whereEqualTo("RestaurantID", restaurantID).whereEqualTo("FoodStatus", "On Sale")
//            .addSnapshotListener() { documents, e ->
//                val foodList: MutableList<Food> = mutableListOf()
//                for (document in documents!!) {
//                    var foodItem= Food(
//                        "tempsave", "tempsave", 0.0, 0, "tempsave",
//                        "test", "tempsave", "tempsave","tempsave")
//
//                    if (document.data != null && document.exists()) {
//
//                        val foodData = Food(
//                            document.data!!["FoodStatus"].toString(),
//                            document.data!!["FoodName"].toString(),
//                            document.data!!["FoodPrice"].toString().toDouble(),
//                            document.data!!["FoodDiscount"].toString().toInt(),
//                            document.data!!["FoodDescription"].toString(),
//                            document.data!!["FoodType"].toString(),
//                            document.data!!["FoodID"].toString(),
//                            document.data!!["RestaurantID"].toString(),
//                            document.data!!["Profile"].toString()
//                        )
//
//                        if (foodList.contains(foodItem)) {
//                            foodList.remove(foodItem)
//                            foodItem = foodData
//                            foodList.add(foodData)
//                        } else {
//                            foodItem = foodData
//                            foodList.add(foodData)
//                        }
//                        foodList.sortBy { it.foodID }
//
//                    }
//
//                    mutableData.value = foodList
//                }
//                containResult.value = documents.size() != 0
//            }
        val reserveList: MutableList<Reservation> = mutableListOf()
        var dbRef:DatabaseReference = FirebaseDatabase.getInstance().getReference("Reservation")
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(v in snapshot.children)
                    {

//                        var reserveData = Reservation("", "","",Calendar.getInstance().getTime() ,Calendar.getInstance().getTime(),"", arrayOf<String>(),
//                        "","","","")
                        var reserveData = v.getValue(Reservation::class.java)
                        reserveList.add(reserveData!!)
                        mutableData.value = reserveList
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
}