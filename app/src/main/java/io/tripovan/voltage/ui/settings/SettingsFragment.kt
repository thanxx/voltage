package io.tripovan.voltage.ui.settings

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.App
import io.tripovan.voltage.R
import io.tripovan.voltage.communication.SocketManager
import io.tripovan.voltage.databinding.FragmentSettingsBinding
import io.tripovan.voltage.ui.settings.devices_list.DevicesAdapter


class SettingsFragment : Fragment() {
    private lateinit var devicesView: RecyclerView
    private lateinit var distanceUnitsSpinner: Spinner
    private lateinit var adapter: DevicesAdapter
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // init settings
        val sharedPref = context?.getSharedPreferences("voltage_settings", Context.MODE_PRIVATE)
        val adapterAddress = sharedPref?.getString("adapter_address", null)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val textView: TextView = binding.selectedDevice
        val root: View = binding.root


        var btDevices = SocketManager.getPairedDevices()
        settingsViewModel.updateDevicesList(btDevices)
        if (btDevices.isEmpty()) {
            textView.text = "Make sure you have paired your OBD2 adapter and/or turned on Bluetooth"
        } else {
            textView.text = "Select OBD2 adapter"
        }

        distanceUnitsSpinner = binding.distanceUnits
        devicesView = binding.devices
        adapter = DevicesAdapter(emptyList(), this)
        devicesView.adapter = adapter
        devicesView.layoutManager = LinearLayoutManager(requireContext())

        settingsViewModel.devicesList.observe(viewLifecycleOwner) { itemList ->
            adapter.updateList(itemList)
        }

        settingsViewModel.text.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        val appInfo = binding.appInfo
        appInfo.text = App.appVersion + "\nhttps://github.com/thanxx/voltage"
        Linkify.addLinks(appInfo, Linkify.WEB_URLS)

        val unitsList = resources.getStringArray(R.array.distance_units).toList()
        val units = sharedPref?.getString("distance_units", unitsList[0])
        distanceUnitsSpinner.setSelection(unitsList.indexOf(units))

        distanceUnitsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem: String = parent.getItemAtPosition(position).toString()
                if (selectedItem != units) {
                    val editor = sharedPref?.edit()
                    editor?.putString("distance_units", selectedItem)
                    editor?.apply()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}