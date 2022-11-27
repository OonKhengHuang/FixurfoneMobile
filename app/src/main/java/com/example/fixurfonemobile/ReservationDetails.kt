package com.example.fixurfonemobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.fixurfonemobile.database.DiagnoseDB
import com.example.fixurfonemobile.database.ReservationDB
import com.example.fixurfonemobile.databinding.ActivityReservationDetailsBinding
import com.example.fixurfonemobile.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReservationDetails : AppCompatActivity() {
    private lateinit var binding: ActivityReservationDetailsBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var diagnoseDBID:String

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
                    binding.status.setTextColor(resources.getColor(R.color.danger))
                }
                "Completed" -> {
                    binding.status.setTextColor(resources.getColor(R.color.green))
                }
                "Attended" -> {
                binding.status.setTextColor(resources.getColor(R.color.yellow))
                }
                "Diagnosis" -> {
                    binding.status.setTextColor(resources.getColor(R.color.lightblue))
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
            var datetoday = GetTodayDate()
            if(reserve.bookingDate!! <= datetoday.getTodayDate() || reserve.status == "Completed")
            {
                binding.btnDelete.visibility = View.GONE
            }

            //***************************************** Diagnosis Details ***************************************************

            if(reserve.status == "Completed")
            {
                binding.diagnoseList.visibility = View.VISIBLE
                var diagnoseList: MutableList<Diagnose> = mutableListOf()
                var compList: MutableList<Component> = mutableListOf()
                var componentList: MutableList<String> = mutableListOf()
                db.getReference("Diagnose").addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            for(v in snapshot.children)
                            {
                                var diagnoseData = v.getValue(Diagnose::class.java)
                                diagnoseList.add(diagnoseData!!)
                            }

                            db.getReference("Component").addValueEventListener(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists())
                                    {
                                        for(v in snapshot.children)
                                        {
                                            var compData = v.getValue(Component::class.java)
                                            compList.add(compData!!)
                                        }

                                        for(v in diagnoseList)
                                        {

                                            if(v.reservationID == reserve.reservationID)
                                            {
                                                if(v.status == "null")
                                                {
                                                    binding.btnRepair.visibility = View.VISIBLE
                                                }else
                                                {
                                                    binding.repairStatusTitle.visibility = View.VISIBLE
                                                    binding.repairStatus.visibility = View.VISIBLE
                                                    binding.repairStatus.text = v.status
                                                    when (v.status) {
                                                        "Pending" -> {
                                                            binding.repairStatus.setTextColor(resources.getColor(R.color.danger))
                                                        }
                                                        "Completed" -> {
                                                            binding.repairStatus.setTextColor(resources.getColor(R.color.green))
                                                        }
                                                        "Waiting for spare parts" -> {
                                                            binding.repairStatus.setTextColor(resources.getColor(R.color.yellow))
                                                        }
                                                        "Repairing is in progress" -> {
                                                            binding.repairStatus.setTextColor(resources.getColor(R.color.lightblue))
                                                        }
                                                        else -> {
                                                            binding.repairStatus.setTextColor(resources.getColor(R.color.danger))
                                                        }
                                                    }
                                                }
                                                diagnoseDBID = v.diagnoseID!!
                                                binding.diagnoseID.text = v.diagnoseID
                                                binding.date.text = dateParse.customParse(v?.date!!)
                                                binding.phoneModel1.text = v.phoneModel
                                                binding.remark3.text = v.remark
                                                binding.totalprice.text = String.format("RM %.2f", v.totalPrice?.toDouble())
                                                for(c in v.componentID!!)
                                                {
                                                    for(b in compList)
                                                    {
                                                        if(c == b.componentID)
                                                        {
                                                            componentList.add(b.cat!!)
                                                        }
                                                    }
                                                }
                                                binding.component.text = componentList.joinToString(", ")
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }


                })

            }

        }.addOnFailureListener {
            //operation upon the failure condition
            Log.e("Firestore", "Error in loading the file: ${it.toString()}")
        }

        binding.btnBack.setOnClickListener {
            val intent =
                Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.btnRepair.setOnClickListener {
            val view = View.inflate(this, R.layout.request_repair_dialog, null)
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
                var diagnoseDB = DiagnoseDB()
                diagnoseDB.updateDiagnose(diagnoseDBID, "Pending")
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this,"Request of repair has been made successfully", Toast.LENGTH_LONG).show()
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

            }
        }

        binding.btnDelete.setOnClickListener{
            val view = View.inflate(this, R.layout.delete_reservation_dialog, null)
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
                var reserveDB = ReservationDB()
                reserveDB.deleteReservation(reserveID)
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