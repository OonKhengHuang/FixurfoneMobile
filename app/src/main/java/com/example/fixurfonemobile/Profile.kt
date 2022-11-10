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
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.fixurfonemobile.databinding.FragmentProfileBinding
import com.example.fixurfonemobile.model.Customer
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Profile : Fragment() {
    private lateinit var custID:String
    private var db:FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var  binding:FragmentProfileBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false )
        val sharedPrefFile = "loginSharedPreferences"
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        custID = sharedPreferences.getString("userUID", null).toString()
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        //logout id unauthorized user login
        if(custID == null){
            val intent = Intent(activity,Login::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this.requireActivity())
        }

        db.getReference("Customer").child(custID!!).get().addOnSuccessListener {
            val cust = it.getValue(Customer::class.java)
            if(cust != null)
            {
                binding.name.text = cust.name
                binding.email.text = cust.email

            }

        }.addOnFailureListener {
            //operation upon the failure condition
            Log.e("Firebase", "Error in loading the file: ${it.toString()}")
        }

        binding.viewDeviceAction.setOnClickListener {
            val intent = Intent(activity, ViewDeviceList::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }

        binding.logoutAction.setOnClickListener {
            editor.clear()
            editor.apply()
            editor.commit()
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(requireActivity())
        }

        binding.viewAboutUsAction.setOnClickListener {
            val intent = Intent(activity, AboutUs::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        return binding.root
    }

}