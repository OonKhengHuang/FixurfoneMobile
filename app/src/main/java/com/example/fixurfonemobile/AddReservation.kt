package com.example.fixurfonemobile

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.fixurfonemobile.database.DiagnoseDB
import com.example.fixurfonemobile.database.ReservationDB
import com.example.fixurfonemobile.databinding.FragmentAddReservationBinding
import com.example.fixurfonemobile.model.Device
import com.example.fixurfonemobile.model.GetTodayDate
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class AddReservation : Fragment() {
    private var valid = true
    private var timeSelected = ""
    private var deviceSelected = ""
    private lateinit var custID: String
    private lateinit var binding: FragmentAddReservationBinding
    private lateinit var arrayAdapter: ArrayAdapter<String>
    var cal = Calendar.getInstance()
    private var thisContext = this.context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_reservation, container, false)
        val sharedPrefFile = "loginSharedPreferences"
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        custID = sharedPreferences.getString("userUID", null).toString()

        //logout id unauthorized user login
        if (custID == null) {
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this.requireActivity())
        }

        //*************************************************** On Create ***************************************************************
        var deviceList: MutableList<String> = mutableListOf()
        var dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Device")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (v in snapshot.children) {
                        var deviceData = v.getValue(Device::class.java)
                        if (deviceData?.custID == custID) {
                            deviceList.add(deviceData.deviceName!!)
                            binding.spinnerPhoneModel.visibility = View.VISIBLE
                            binding.phoneModel.visibility = View.GONE
                        }
                    }

                    deviceList.add("Others")

                    val spinner = binding.spinnerPhoneModel
                    try {
                        arrayAdapter = ArrayAdapter<String>(
                            activity?.application!!,
                            android.R.layout.simple_spinner_dropdown_item,
                            deviceList
                        )
                        spinner.adapter = arrayAdapter
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {
                                    //Toast.makeText(applicationContext,"selected bank is = "+bank[p2], Toast.LENGTH_SHORT).show()
                                    deviceSelected = deviceList[p2]
                                    if (deviceSelected == "Others") {
                                        binding.phoneModel.visibility = View.VISIBLE
                                    } else {
                                        binding.phoneModel.visibility = View.GONE
                                    }
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }

                            }
                    } catch (ex: Exception) {

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        val selectTime = arrayOf("10:00 AM", "12:00 PM", "2:00 PM", "4:00 PM")

        val spinner = binding.spinner
        val arrayAdapter = ArrayAdapter<String>(
            this.requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            selectTime
        )
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Toast.makeText(applicationContext,"selected bank is = "+bank[p2], Toast.LENGTH_SHORT).show()
                timeSelected = selectTime[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        //***********************************************Book Reservation**********************************************************
        binding.submit.setOnClickListener {
            textValidation()
            if (valid) {
                val fomatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                val date = fomatter.format(cal.timeInMillis)
                var phone = ""
                if (deviceSelected == "Others") {
                    phone = binding.phoneModel.text.toString()
                } else {
                    phone = deviceSelected
                }

                if (deviceSelected == null || deviceSelected == "") {
                    phone = binding.phoneModel.text.toString()
                }

                val time = timeSelected
                var remark = ""
                var serviceType: MutableList<String> = mutableListOf()
                if (!TextUtils.isEmpty(binding.remark.text.toString())) {
                    remark = binding.remark.text.toString()
                }
                if (binding.battery.isChecked) {
                    serviceType.add("Battery Replacement")
                }
                if (binding.screen.isChecked) {
                    serviceType.add("Screen Service")
                }
                if (binding.camera.isChecked) {
                    serviceType.add("Camera Replacement")
                }
                if (binding.speaker.isChecked) {
                    serviceType.add("Speaker/Microphone")
                }
                if (binding.others.isChecked) {
                    serviceType.add("Others")
                }
                var datenow: GetTodayDate = GetTodayDate()
                val reserve = Reservation(
                    "RE" + UUID.randomUUID().toString().replace("-", ""),
                    custID,
                    null,
                    datenow.getTodayDate(),
                    date,
                    time,
                    serviceType,
                    phone,
                    if (remark == "") null else remark,
                    "Pending",
                    "Pending"
                )
                var db = ReservationDB()
                db.addReservation(reserve)
                val intent = Intent(activity, MainActivity::class.java)

                //************************************* Dialog ********************************************************
                val view =
                    View.inflate(this.requireActivity(), R.layout.reservation_success_dialog, null)
                val builder = AlertDialog.Builder(this.requireActivity())
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                var btnCancel: Button = view.findViewById(R.id.btn_cancel)
                Toast.makeText(
                    this.requireActivity(),
                    "Reservation Added successful",
                    Toast.LENGTH_SHORT
                ).show()
                btnCancel.setOnClickListener {
                    dialog.dismiss()
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }


            }

        }
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal.set(year, month, dayOfMonth)
                val fomatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                binding.bookingDate.setText(fomatter.format(cal.timeInMillis))

            }
        }

        binding.bookingDate.setOnClickListener {
            DatePickerDialog(
                this.requireContext(), R.style.DialogTheme,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()

        }
        return binding.root
    }

    private fun textValidation() {
        val fomatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val date = fomatter.format(cal.timeInMillis)
        val datenow = GetTodayDate()
        if (TextUtils.isEmpty(binding.bookingDate.text.toString())) {
            binding.bookingDate.setError("Booking Date is required!")
            valid = valid && false
        } else {
            valid = true
        }

        if (TextUtils.isEmpty(binding.phoneModel.text.toString())) {
            if (deviceSelected == "Others") {
                binding.phoneModel.setError("Phone Model is required!")
                valid = valid && false
            }
        }

        if (binding.screen.isChecked || binding.battery.isChecked || binding.camera.isChecked || binding.speaker.isChecked || binding.others.isChecked) {
            valid = valid && true

        } else {
            binding.selection.setError("Please select a problem(s)!")
            valid = valid && false
        }
        if (date < datenow.getTodayDate()) {
            binding.bookingDate.setError("Booking Date must be after today!")
            valid = valid && false
        }


    }


}