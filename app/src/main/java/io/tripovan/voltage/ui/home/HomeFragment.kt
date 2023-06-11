package io.tripovan.voltage.ui.home

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.tripovan.voltage.databinding.FragmentHomeBinding
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat.startForegroundService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.MainActivity
import io.tripovan.voltage.bluetooth.BluetoothManager
import io.tripovan.voltage.bluetooth.BluetoothService
import io.tripovan.voltage.ui.home.devices_list.DevicesAdapter

class HomeFragment : Fragment() {
    private lateinit var devicesView: RecyclerView
    private lateinit var adapter: DevicesAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var bluetoothService: BluetoothService
    private var bound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BluetoothService.LocalBinder
            bluetoothService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        // init settings
        val sharedPref = context?.getSharedPreferences("voltage_settings", Context.MODE_PRIVATE)
        val adapterAddress = sharedPref?.getString("adapter_address", null)
        Log.i("MAIN", String.format("adapter: %s", adapterAddress))


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val button = binding.connect
        button.visibility = View.GONE

        val bluetoothManager = BluetoothManager()
        homeViewModel.updateDevicesList(bluetoothManager.getPairedDevices())


        if (adapterAddress != null) {
            homeViewModel.updateSelectedDevice(adapterAddress)
//            button.visibility = View.VISIBLE
//            val bluetoothServiceIntent = Intent(context, BluetoothService::class.java)
//            bluetoothServiceIntent.putExtra("address", adapterAddress)
//            button.setOnClickListener {
//
//            }
//        } else {
//            button.visibility = View.GONE
        }




        devicesView = binding.devices
        adapter = DevicesAdapter(emptyList(), this) // Pass an empty list initially
        devicesView.adapter = adapter
        devicesView.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.devicesList.observe(viewLifecycleOwner) { itemList ->
            adapter.updateList(itemList)
        }

        val selectedDevice: TextView = binding.selectedDevice
        homeViewModel.selectedDevice.observe(viewLifecycleOwner) { device ->
            selectedDevice.text = device
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}