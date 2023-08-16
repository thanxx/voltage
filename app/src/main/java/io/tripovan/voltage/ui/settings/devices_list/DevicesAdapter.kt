package io.tripovan.voltage.ui.settings.devices_list

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.res.Resources.Theme
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.App
import io.tripovan.voltage.R
import io.tripovan.voltage.ui.settings.SettingsViewModel

class DevicesAdapter(private var dataList: List<BluetoothDevice>, var fragment: Fragment) :
    RecyclerView.Adapter<DevicesViewHolder>() {

    lateinit var context: Context
    private val settingsViewModel =
        ViewModelProvider(fragment).get(SettingsViewModel::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device, parent, false)
        return DevicesViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        holder.nameView.text = dataList[position].name
        holder.addressView.text = dataList[position].address


        val typedValue = TypedValue()
        val theme: Theme = holder.nameView.context.theme
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSecondary,
            typedValue,
            true
        )
        val color = typedValue.data
        theme.resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true)
        val accentColor = typedValue.data

        val adapterAddress = dataList[position].address
        val sharedPref = App.instance.getSharedPrefs()!!
        val selectedAddress = sharedPref.getString("adapter_address", null)
        if (selectedAddress != null) {

            if (dataList[position].address == selectedAddress) {
                holder.nameView.setTextColor(accentColor)
                holder.addressView.setTextColor(accentColor)
            } else {
                holder.nameView.setTextColor(color)
                holder.addressView.setTextColor(color)
            }
        }

        holder.itemView.setOnClickListener {

            val editor = sharedPref.edit()
            editor.putString("adapter_address", adapterAddress)
            editor.apply()
            settingsViewModel.updateText(adapterAddress)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateList(list: List<BluetoothDevice>) {
        dataList = list
        notifyDataSetChanged()
    }
}
