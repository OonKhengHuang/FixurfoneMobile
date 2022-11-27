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
import com.example.fixurfonemobile.model.Diagnose
import com.example.fixurfonemobile.model.Reservation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        private var db:FirebaseDatabase = FirebaseDatabase.getInstance()
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
                binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.red))
            }else if(item.status == "Completed")
            {
                db.getReference("Diagnose").addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists())
                        {
                            for(v in snapshot.children)
                            {
                                var diagnoseData = v.getValue(Diagnose::class.java)
                                if(diagnoseData?.reservationID == item.reservationID)
                                {
                                    if(diagnoseData?.status == "Pending")
                                    {
                                        binding.status.text = "Repair Pending"
                                        binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.red))
                                    }else if(diagnoseData?.status == "Completed")
                                    {
                                        binding.status.text = "Repair Completed"
                                        binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.green))
                                    }else if(diagnoseData?.status == "Waiting for spare parts")
                                    {
                                        binding.status.text = diagnoseData.status
                                        binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.yellow))
                                    }else if(diagnoseData?.status == "Repairing is in progress")
                                    {
                                        binding.status.text = diagnoseData.status
                                        binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.lightblue))
                                    }else
                                    {
                                        binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.green))
                                    }

                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }else if(item.status == "Attended")
            {
                binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.yellow))
            }else if(item.status == "Diagnosis")
            {
                binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.lightblue))
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