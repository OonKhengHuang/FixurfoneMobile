package com.example.fixurfonemobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fixurfonemobile.databinding.DeviceListItemBinding
import com.example.fixurfonemobile.model.Device

class DeviceItemListAdapter(val clickLis: DeviceItemListListener) : ListAdapter<Device, DeviceItemListAdapter.ViewHolder>(DeviceItemDiffCallback())  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceItemListAdapter.ViewHolder {
        return DeviceItemListAdapter.ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DeviceItemListAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickLis)

    }

    class ViewHolder private constructor(val binding: DeviceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Device, clickLis: DeviceItemListListener) {
            binding.device = item
            binding.clickListener = clickLis
            binding.deviceName.text = item.deviceName
            binding.manufacturer.text = item.manufacturer

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DeviceListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class DeviceItemDiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.deviceID == newItem.deviceID
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}

class DeviceItemListListener(val clickListener: (deviceID: String?) -> Unit) {
    fun onClick(device: Device) {
        clickListener(device.deviceID)

    }
}