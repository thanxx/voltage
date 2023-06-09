package io.tripovan.voltage.ui.home

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HomeViewModel : ViewModel() {

    private val _devicesList = MutableLiveData<List<BluetoothDevice>>()
    private val _selectedDevice = MutableLiveData<String>()

    val devicesList: LiveData<List<BluetoothDevice>>
        get() = _devicesList

    val selectedDevice: LiveData<String> get() = _selectedDevice

    fun updateDevicesList(data: List<BluetoothDevice>) {
        _devicesList.value = data
    }

    fun updateSelectedDevice(device: String) {
        _selectedDevice.value = "Selected device: $device"
    }
}