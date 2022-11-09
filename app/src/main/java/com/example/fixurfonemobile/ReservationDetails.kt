package com.example.fixurfonemobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.fixurfonemobile.databinding.ActivityReservationDetailsBinding
import com.example.fixurfonemobile.model.DateTimeParser
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.FirebaseDatabase

class ReservationDetails : AppCompatActivity() {
    private lateinit var binding: ActivityReservationDetailsBinding
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reservation_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        getSupportActionBar()?.setBackgroundDrawable(ColorDrawable(getResources().getColor(R.color.white)))

        val reserveID = intent.getStringExtra("ReservationID")
        db = FirebaseDatabase.getInstance()
        db.getReference("Reservation").child(reserveID!!).get().addOnSuccessListener {
            val reserve = it.getValue(Reservation::class.java)
            val dateParse = DateTimeParser()
            binding.reserveID.text = reserve?.reservationID
            binding.bookingDate.text =  dateParse.customParse(reserve?.bookingDate!!)
            binding.bookingTime.text = reserve.bookingTime
            binding.phoneModel.text = reserve.phoneModel
            binding.repairStatus.text = reserve.repairStatus
            binding.status.text = reserve.status
            binding.serviceType.text = reserve.serviceType?.joinToString(", ")
            when (reserve.status) {
                "Pending" -> {
                    binding.status.setTextColor(resources.getColor(R.color.yellow))
                }
                "Completed" -> {
                    binding.status.setTextColor(resources.getColor(R.color.green))
                }
                else -> {
                    binding.status.setTextColor(resources.getColor(R.color.danger))
                }
            }
            if(reserve.remark != "")
            {
                binding.remark1.text = reserve.remark
                binding.remark.visibility = View.VISIBLE
                binding.remark1.visibility = View.VISIBLE
            }

        }.addOnFailureListener {
            //operation upon the failure condition
            Log.e("Firestore", "Error in loading the file: ${it.toString()}")
        }

        binding.btnBack.setOnClickListener {
            val intent =
                Intent(this, MainActivity::class.java)
//            intent.putExtra("sellerID", sellerID)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
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