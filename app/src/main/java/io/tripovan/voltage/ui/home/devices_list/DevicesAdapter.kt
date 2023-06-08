package io.tripovan.voltage.ui.home.devices_list

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.R

class DevicesAdapter(private var dataList: List<BluetoothDevice>) : RecyclerView.Adapter<DevicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device, parent, false)
        return DevicesViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        holder.nameView.text = dataList[position].name
        holder.addressView.text = dataList[position].address
        holder.itemView.setOnClickListener {
            // Handle item click here
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateList(list: List<BluetoothDevice>){
        dataList = list
        notifyDataSetChanged()
    }

}
