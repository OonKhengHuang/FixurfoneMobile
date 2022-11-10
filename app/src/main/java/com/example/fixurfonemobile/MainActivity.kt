package com.example.fixurfonemobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
    private lateinit var custID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        bottomNav = binding.bottomNav

        val navController = findNavController(R.id.fragmentContainerView2)
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        val sharedPrefFile = "loginSharedPreferences"
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        custID = sharedPreferences.getString("userUID", null).toString()

        //logout id unauthorized user login
        if(custID == null){
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
        }


    }


}