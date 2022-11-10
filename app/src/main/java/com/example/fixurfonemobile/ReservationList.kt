package com.example.fixurfonemobile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fixurfonemobile.adapter.ReservationItemListAdapter
import com.example.fixurfonemobile.adapter.ReservationItemListListener
import com.example.fixurfonemobile.databinding.FragmentReservationListBinding
import com.google.firebase.database.FirebaseDatabase

class ReservationList : Fragment() {

    private lateinit var reservationItemViewModel: ReservationItemViewModel
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var custID:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentReservationListBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_reservation_list, container, false )
        //Get data from login shared preferences
        val sharedPrefFile = "loginSharedPreferences"
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        custID = sharedPreferences.getString("userUID", null).toString()

        //logout id unauthorized user login
        if(custID == null){
            val intent = Intent(requireContext(),Login::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(requireActivity())
        }


        //*************************************************** Recycle View *************************************************************
        var application = requireNotNull(this.activity).application
        var adapter =  ReservationItemListAdapter( ReservationItemListListener {

            reservationItemViewModel.onItemClicked(it)
        }, application, this.requireContext())

        binding.reservationListRecycle.adapter = adapter

        val viewModelFactory = ReservationItemViewModelFactory(db, application, custID) //<----------

        // Get a reference to the ViewModel associated with this fragment.
        reservationItemViewModel = ViewModelProvider(this, viewModelFactory).get(ReservationItemViewModel::class.java)

        binding.reservationItemViewModel = reservationItemViewModel
        reservationItemViewModel.containResult.observe(viewLifecycleOwner, Observer{
            if (it){
                binding.noResult.visibility = View.GONE
            }else{
                binding.noResult.visibility = View.VISIBLE
            }

        })
        reservationItemViewModel.mutableData.observe(viewLifecycleOwner, Observer{
            it?.let {
                adapter.submitList(it)
            }

        })
        reservationItemViewModel.navigateToItemDetails.observe(viewLifecycleOwner,Observer{
            it?.let {
                //StartActivity
                val intent = Intent(activity, ReservationDetails::class.java)
                intent.putExtra("ReservationID", it)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
                reservationItemViewModel.onItemNavigated()
            }
        })
        binding.lifecycleOwner = this
        binding.swipeRefreshLayout.setOnRefreshListener {

            // on below line we are setting is refreshing to false.
            binding.swipeRefreshLayout.isRefreshing = false
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)

        }
        return binding.root
    }


}