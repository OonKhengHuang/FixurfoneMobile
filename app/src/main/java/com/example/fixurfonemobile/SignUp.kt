package com.example.fixurfonemobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.fixurfonemobile.database.CustomerDB
import com.example.fixurfonemobile.databinding.ActivitySignUpBinding
import com.example.fixurfonemobile.model.Customer
import com.example.fixurfonemobile.model.PasswordHash
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.SecureRandom
import java.util.*

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var db: FirebaseDatabase
    private val RANDOM: Random = SecureRandom()
    private var valid = true
    private var SignedUp = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
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

        binding.signup.setOnClickListener {
            textValidation()
            var email = binding.email.text.trim().toString().lowercase()
            var found = false
            if (valid) {
                db.getReference("Customer").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (v in snapshot.children) {
                                var custData = v.getValue(Customer::class.java)
                                if (custData?.email == email) {
                                    found = true
                                }
                            }

                            if (!found) {
                                processSignUp()
                            } else {
                                if(SignedUp == false){
                                    binding.email.setError("Email already exists!")
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

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processSignUp() {
        var hash = PasswordHash()
        val name = binding.name.text.toString()
        val email = binding.email.text.toString().lowercase()
        val address = binding.address.text.toString()
        val phoneNum = binding.phoneNum.text.toString()
        val buff = ByteArray(10)
        RANDOM.nextBytes(buff)
        var salt: String = Base64.getEncoder().encodeToString(buff)
        val pass = hash.encryptSHA(binding.password.text.toString().trim() + salt)

        var customer = Customer(
            "CT" + UUID.randomUUID().toString().replace("-", ""), name, phoneNum,
            address, email, pass, 1, "no", salt
        )
        var db = CustomerDB()
        db.addCustomer(customer)
        SignedUp = true
        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()


        val sharedPrefFile = "loginSharedPreferences"
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()


        editor.putString("userUID", customer.custID)
        editor.apply()
        editor.commit()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun textValidation() {

        if (TextUtils.isEmpty(binding.name.text.toString())) {
            binding.name.setError("Name is required!")
            valid = valid && false
        } else {
            if (!binding.name.text.toString().matches("^(?![\\s.]+\$)[a-zA-Z\\s.]*\$".toRegex())) {
                binding.name.setError("Name cannot contain number or special character")
                valid = valid && false
            } else {
                valid = true
            }
        }

        if (TextUtils.isEmpty(binding.email.text.toString())) {
            binding.email.setError("Email is required!")
            valid = valid && false
        } else {
            if (!binding.email.text.toString()
                    .matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\$".toRegex())
            ) {
                binding.email.setError("Invalid email format")
                valid = valid && false
            }
        }


        if (TextUtils.isEmpty(binding.phoneNum.text.toString())) {
            binding.phoneNum.setError("Phone Number is required!")
            valid = valid && false
        } else {
            if (!binding.phoneNum.text.toString()
                    .matches("^(01)[0-46-9]-*[0-9]{7,8}\$".toRegex())
            ) {
                binding.phoneNum.setError("Invalid Phone Number: 01XXXXXXXX")
                valid = valid && false
            }
        }

        if (TextUtils.isEmpty(binding.address.text.toString())) {
            binding.address.setError("Address is required!")
            valid = valid && false
        }

        if (TextUtils.isEmpty(binding.password.text.toString())) {
            binding.password.setError("Password is required!")
            valid = valid && false
        } else {
            if (!binding.password.text.toString()
                    .matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,100}\$".toRegex())
            ) {
                binding.password.setError("Password length must be between 8 and 100 characters, at least one number, lower case, upper case and special characters")
                valid = valid && false
            } else {
                if (binding.password.text.toString() != binding.confirmPass.text.toString()) {
                    binding.password.setError("Password does not match!")
                    binding.confirmPass.setError("Password does not match!")
                    valid = valid && false
                }
            }
        }

        if (TextUtils.isEmpty(binding.confirmPass.text.toString())) {
            binding.confirmPass.setError("Confirm Password is required!")
            valid = valid && false
        }


    }
}