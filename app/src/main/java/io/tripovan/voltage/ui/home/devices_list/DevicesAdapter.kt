package io.tripovan.voltage.ui.home.devices_list

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.R
import io.tripovan.voltage.ui.home.HomeViewModel

class DevicesAdapter(private var dataList: List<BluetoothDevice>,fragment: Fragment) : RecyclerView.Adapter<DevicesViewHolder>() {

    lateinit var context: Context
    val homeViewModel =
        ViewModelProvider(fragment).get(HomeViewModel::class.java)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device, parent, false)
        return DevicesViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        holder.nameView.text = dataList[position].name
        holder.addressView.text = dataList[position].address
        val adapterAddress = dataList[position].address
        holder.itemView.setOnClickListener {

            val sharedPref = context.getSharedPreferences("voltage_settings", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("adapter_address", adapterAddress)
            editor.apply()
            homeViewModel.updateSelectedDevice(adapterAddress)
            Toast.makeText(context, dataList[position].name, Toast.LENGTH_SHORT).show()
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
