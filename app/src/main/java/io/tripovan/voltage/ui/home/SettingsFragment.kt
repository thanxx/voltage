package io.tripovan.voltage.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.App
import io.tripovan.voltage.databinding.FragmentSettingsBinding
import io.tripovan.voltage.ui.home.devices_list.DevicesAdapter

class SettingsFragment : Fragment() {
    private lateinit var devicesView: RecyclerView
    private lateinit var adapter: DevicesAdapter
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        // init settings
        val sharedPref = context?.getSharedPreferences("voltage_settings", Context.MODE_PRIVATE)
        val adapterAddress = sharedPref?.getString("adapter_address", null)
        Log.i("MAIN", String.format("adapter: %s", adapterAddress))


        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val button = binding.connect
        button.visibility = View.GONE


        var bluetoothManager = App.instance.getBluetoothManager()


        if (bluetoothManager != null) {
            settingsViewModel.updateDevicesList(bluetoothManager.getPairedDevices())
            if (adapterAddress != null) {
                settingsViewModel.updateText("Selected adapter: $adapterAddress")
            }

        } else {
            settingsViewModel.updateDevicesList(emptyList())
            settingsViewModel.updateText("Turn on Bluetooth to select adapter")
        }


        devicesView = binding.devices
        adapter = DevicesAdapter(emptyList(), this)
        devicesView.adapter = adapter
        devicesView.layoutManager = LinearLayoutManager(requireContext())

        settingsViewModel.devicesList.observe(viewLifecycleOwner) { itemList ->
            adapter.updateList(itemList)
        }

        val textView: TextView = binding.selectedDevice
        settingsViewModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}