package com.example.fixurfonemobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fixurfonemobile.databinding.FragmentReservationListBinding

class ReservationList : Fragment() {
    private lateinit var reserveRecyleView: RecyclerView
    private lateinit var tvLoadingDat: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentReservationListBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_reservation_list, container, false )

        reserveRecyleView = binding.reservationListRecycle
        reserveRecyleView.layoutManager = LinearLayoutManager(this.context)
        reserveRecyleView.setHasFixedSize(true)
        tvLoadingDat = binding.noResult
        return binding.root
    }


}