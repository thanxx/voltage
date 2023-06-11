package io.tripovan.voltage.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

class BluetoothManager {
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDevice> {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val deviceList = mutableListOf<BluetoothDevice>()
        pairedDevices?.let {
            for (device: BluetoothDevice in it) {
                deviceList.add(device)
            }
        }
        return deviceList
    }
}