package com.example.fixurfonemobile.adapter

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fixurfonemobile.R
import com.example.fixurfonemobile.databinding.ReservationListItemBinding
import com.example.fixurfonemobile.model.DateTimeParser
import com.example.fixurfonemobile.model.Reservation
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReservationItemListAdapter (val clickLis: ReservationItemListListener, val app: Application, val context1:Context) : ListAdapter<Reservation, ReservationItemListAdapter.ViewHolder>(ReservationItemDiffCallback())  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservationItemListAdapter.ViewHolder {
        return ReservationItemListAdapter.ViewHolder.from(parent,app,context1)
    }

    override fun onBindViewHolder(holder: ReservationItemListAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickLis)

    }

    class ViewHolder private constructor(val binding: ReservationListItemBinding, val app: Application, var context:Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Reservation, clickLis: ReservationItemListListener) {
            binding.reserve = item
            binding.clickListener = clickLis
            binding.phoneModel.text = item.phoneModel
            var dateParse: DateTimeParser = DateTimeParser()
            binding.bookingDate.text = dateParse.customParse(item.bookingDate!!)
            var serviceType: List<String> = item.serviceType!!
            binding.serviceType.text = if (serviceType.size > 1) serviceType[0]+", ..." else serviceType[0]
            binding.status.text = item.status
            if(item.status == "Pending"){
                binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.yellow))
            }else if(item.status == "Completed")
            {
                binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.green))
            }else{
                binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.danger))
            }



        }

        companion object {
            fun from(parent: ViewGroup, app: Application, context:Context): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReservationListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding,app,context)
            }
        }
    }
}

class ReservationItemDiffCallback : DiffUtil.ItemCallback<Reservation>() {
    override fun areItemsTheSame(oldItem: Reservation, newItem: Reservation): Boolean {
        return oldItem.reservationID == newItem.reservationID
    }

    override fun areContentsTheSame(oldItem: Reservation, newItem: Reservation): Boolean {
        return oldItem == newItem
    }
}

class ReservationItemListListener(val clickListener: (reservationID: String?) -> Unit) {
    fun onClick(reserve: Reservation) {
        clickListener(reserve.reservationID)

    }
}