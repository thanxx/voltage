package io.tripovan.voltage.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.tripovan.voltage.databinding.FragmentHomeBinding
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.ui.home.devices_list.DevicesAdapter

class HomeFragment : Fragment() {
    private lateinit var devicesView: RecyclerView
    private lateinit var adapter: DevicesAdapter

    @SuppressLint("MissingPermission")
    fun listPairedBluetoothDevices(context: Context): List<BluetoothDevice> {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val deviceList = mutableListOf<BluetoothDevice>()
        pairedDevices?.let {
            for (device: BluetoothDevice in it) {
                deviceList.add(device)
            }
        }
        return deviceList
    }

    // Example usage


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun getPairedDevices(): List<BluetoothDevice> {
        if (context?.let { it1 ->
                ContextCompat.checkSelfPermission(
                    it1,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            } == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                        it1,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        2
                    )
                }
            }
            Toast.makeText(requireContext(), "Granted!", Toast.LENGTH_SHORT).show()
            return listPairedBluetoothDevices(requireContext())
        }
        return emptyList()
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
//        val button = binding.button
        homeViewModel.updateDevicesList(getPairedDevices())
        if (adapterAddress != null) {
            homeViewModel.updateSelectedDevice(adapterAddress)
        }


//        button.setOnClickListener {
//            homeViewModel.updateDevicesList(getPairedDevices())
//        }

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