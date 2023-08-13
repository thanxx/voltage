package io.tripovan.voltage.ui.settings.devices_list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.R

class DevicesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val nameView: TextView = itemView.findViewById(R.id.name)
    val addressView: TextView = itemView.findViewById(R.id.address)
}

