package com.example.fixurfonemobile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fixurfonemobile.adapter.ReservationItemListAdapter
import com.example.fixurfonemobile.adapter.ReservationItemListListener
import com.example.fixurfonemobile.databinding.FragmentReservationListBinding
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.FirebaseDatabase

class ReservationList : Fragment() {

    private lateinit var reservationItemViewModel: ReservationItemViewModel
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentReservationListBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_reservation_list, container, false )

        var application = requireNotNull(this.activity).application
        var adapter =  ReservationItemListAdapter( ReservationItemListListener {

            reservationItemViewModel.onItemClicked(it)
        }, application)

        binding.reservationListRecycle.adapter = adapter

        val viewModelFactory = ReservationItemViewModelFactory(db, application, "")

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
                val intent = Intent(activity, Reservation::class.java)
                intent.putExtra("reservationID", it)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
                reservationItemViewModel.onItemNavigated()
            }
        })
        binding.lifecycleOwner = this
        return binding.root
    }


}