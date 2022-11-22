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
import com.example.fixurfonemobile.model.PasswordHash
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
            var hash = PasswordHash()

            if (email != "" && email != null && password != "" && password != null) {
                binding.loginEmailError.visibility = View.GONE
                binding.loginError.visibility = View.GONE
                var custID:String = ""
                db.getReference("Customer").addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            for(v in snapshot.children)
                            {
                                var custData = v.getValue(Customer::class.java)
                                var passbyte = password.trim() + custData?.salt
                                if(custData?.email == email.trim() && custData?.password == hash.encryptSHA(passbyte)){
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
                if(email == "" || email == null)
                {
                    binding.loginEmailError.visibility = View.VISIBLE
                    binding.loginEmailError.text = "Email cannot be empty!"
                }
               if(password == "" || password == null)
               {
                   binding.loginError.visibility = View.VISIBLE
                   binding.loginError.text = "Password cannot be empty!"
               }


            }

        }
        binding.signupBtn.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
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