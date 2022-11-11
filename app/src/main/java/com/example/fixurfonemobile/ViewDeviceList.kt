package com.example.fixurfonemobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fixurfonemobile.adapter.DeviceItemListAdapter
import com.example.fixurfonemobile.adapter.DeviceItemListListener
import com.example.fixurfonemobile.databinding.ActivityViewDeviceListBinding
import com.google.firebase.database.FirebaseDatabase

class ViewDeviceList : AppCompatActivity() {
    private lateinit var deviceItemViewModel: DeviceItemViewModel
    private lateinit var custID:String
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding:ActivityViewDeviceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_device_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Get data from login shared preferences
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

        //*************************************************** Recycle View *************************************************************
        var adapter = DeviceItemListAdapter(DeviceItemListListener {

            deviceItemViewModel.onItemClicked(it)
        })

        binding.deviceListRecycle.adapter = adapter

        val application = requireNotNull(this).application
        val viewModelFactory = DeviceItemViewModelFactory(db, application, custID)

        // Get a reference to the ViewModel associated with this fragment.
        deviceItemViewModel = ViewModelProvider(this, viewModelFactory).get(DeviceItemViewModel::class.java)

        binding.deviceItemViewModel = deviceItemViewModel
        deviceItemViewModel.containResult.observe(this, Observer{
            if (it){
                binding.noResult.visibility = View.GONE
            }else{
                binding.noResult.visibility = View.VISIBLE
            }

        })
        deviceItemViewModel.mutableData.observe(this, Observer{
            it?.let {
                adapter.submitList(it)
            }

        })
        deviceItemViewModel.navigateToItemDetails.observe(this,Observer{
            it?.let {
                //StartActivity
                val intent = Intent(this, DeviceDetails::class.java)
                intent.putExtra("deviceID", it)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
                deviceItemViewModel.onItemNavigated()
            }
        })

        binding.lifecycleOwner = this


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