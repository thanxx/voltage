package io.tripovan.voltage.ui.settings

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.tripovan.voltage.App
import io.tripovan.voltage.R
import io.tripovan.voltage.communication.SocketManager
import io.tripovan.voltage.databinding.FragmentSettingsBinding
import io.tripovan.voltage.ui.settings.devices_list.DevicesAdapter
import io.tripovan.voltage.utils.Constants
import io.tripovan.voltage.utils.LoggingUtils
import io.tripovan.voltage.utils.MailUtils


class SettingsFragment : Fragment() {
    private lateinit var devicesView: RecyclerView
    private lateinit var distanceUnitsSpinner: Spinner
    private lateinit var adapter: DevicesAdapter
    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        // init settings
        val sharedPref = App.instance.getSharedPrefs()

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root



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

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val sendLogsTextView = binding.exportLogs as TextView

        sendLogsTextView.setOnClickListener {
            try {
                LoggingUtils().dumpLogs()
                MailUtils().sendLogs()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }

        if (sharedPref?.getBoolean("firstRun", true) == true) {
            showFirstRunAlertDialog()
        }
        return root
    }

    private fun showFirstRunAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Welcome!")
        alertDialogBuilder.setMessage("This app was tested with the 2016 Volt. If you experience errors, please send logs, open an issue on GitHub (in Settings). Also, this app is open source, so you are welcome to make a pull request or provide feedback")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            var editor = App.instance.getSharedPrefs()?.edit()
            editor?.putBoolean("firstRun", false)
            editor?.apply()
        }
        alertDialogBuilder.setCancelable(false)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Log.i("", "onResume")

        val textView: TextView = binding.textView


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (App.instance.isBtPermissionEnabled()
            ) {
                updateDevices()
                textView.isClickable = false
            } else {
                val textView: TextView = binding.textView
                textView.isClickable = true
                binding.textView.text =
                    "In order to select an OBD2 adapter, you need to grant permissions. Click here to request them"
                textView.setOnClickListener {
                    Log.i(Constants.TAG, "Request permissions clicked")
                    requestPermissions()
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.BLUETOOTH_CONNECT
                        )
                    ) {
                        binding.textView.text = "This app needs permission for scanning nearby devices over Bluetooth, if you reject, the app will not be able read data from your vehicle"
                    } else {
                        binding.textView.text = "Permission for scanning nearby devices is disabled. You can enable it in the Android settings for this app"
                    }
                }
            }
        } else {
            updateDevices()
        }
    }

    private fun updateDevices() {
        val btDevices = SocketManager.getPairedDevices()
        settingsViewModel.updateDevicesList(btDevices)
        if (btDevices.isEmpty()) {
            binding.textView.text = "No paired devices"
        } else {
            binding.textView.text = "Select OBD2 adapter"
        }
    }


    private fun requestPermissions() {

        var permissionsToRequire: ArrayList<String> = ArrayList()
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT
        ).forEach {
            if ((ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) == PackageManager.PERMISSION_DENIED)
            ) {
                permissionsToRequire.add(it)
            }
        }

        val permissionsArray = permissionsToRequire.toTypedArray()
        ActivityCompat.requestPermissions(requireActivity(), permissionsArray, 1)

    }


}