package com.example.fixurfonemobile.adapter

import android.app.Application
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fixurfonemobile.databinding.ReservationListItemBinding
import com.example.fixurfonemobile.model.Reservation
import java.io.File

class ReservationItemListAdapter (val clickLis: ReservationItemListListener, val app: Application) : ListAdapter<Reservation, ReservationItemListAdapter.ViewHolder>(ReservationItemDiffCallback())  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservationItemListAdapter.ViewHolder {
        return ReservationItemListAdapter.ViewHolder.from(parent,app)
    }

    override fun onBindViewHolder(holder: ReservationItemListAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickLis)
    }

    class ViewHolder private constructor(val binding: ReservationListItemBinding, val app: Application) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Reservation, clickLis: ReservationItemListListener) {
            binding.reserve = item
            binding.clickListener = clickLis
            binding.restaurantName.text = item.reservationID

        }

        companion object {
            fun from(parent: ViewGroup, app: Application): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReservationListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding,app)
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