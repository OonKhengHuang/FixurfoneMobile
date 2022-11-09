package com.example.fixurfonemobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.fixurfonemobile.database.CustomerDB
import com.example.fixurfonemobile.databinding.ActivityLoginBinding
import com.example.fixurfonemobile.model.Customer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        db = FirebaseDatabase.getInstance()
        val sharedPrefFile = "loginSharedPreferences"
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val sharedPrefuserUID = sharedPreferences.getString("userUID", null)
        val sharedPrefEmail = sharedPreferences.getString("email", null)
        if (sharedPrefuserUID != null && sharedPrefuserUID != "") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.loginBtn.setOnClickListener {

            var email = binding.editTextEmail.text.toString()
            var password = binding.editTextPassword.text.toString()


            if (email != "" && email != null) {
                var custID:String = ""
                db.getReference("Customer").addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            for(v in snapshot.children)
                            {
                                var custData = v.getValue(Customer::class.java)
                                if(custData?.email == email && custData?.password == password){
                                    custID = custData.custID!!
                                }
                            }
                            loginSuccess(custID)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })


            } else {
                binding.loginEmailError.visibility = View.VISIBLE
                binding.loginEmailError.text = "Email cannot be left empty!"
            }

        }

    }

    fun loginSuccess(custID: String){
        val sharedPrefFile = "loginSharedPreferences"
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        if (custID != "") {
            editor.putString("userUID", custID)
            editor.apply()
            editor.commit()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            binding.loginError.visibility = View.VISIBLE
            binding.loginError.text = "Email or password error!"
        }
    }
}