package com.example.fixurfonemobile

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.fixurfonemobile.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        bottomNav = binding.bottomNav

        val navController = findNavController(R.id.fragmentContainerView2)
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)

//        db = FirebaseDatabase.getInstance()
//
//
//        db.getReference("Customer").child("CT185f2acbd0f343709aec61aba46dd160").get().addOnSuccessListener {
//            val cust = it.getValue(Customer::class.java)
//            binding.txtTest.text = cust?.email.toString()
//        }

    }

}