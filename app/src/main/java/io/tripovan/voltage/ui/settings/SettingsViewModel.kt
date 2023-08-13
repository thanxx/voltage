package io.tripovan.voltage.ui.settings

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SettingsViewModel : ViewModel() {

    private val _devicesList = MutableLiveData<List<BluetoothDevice>>()
    private val _selectedDevice = MutableLiveData<String>()

    val devicesList: LiveData<List<BluetoothDevice>>
        get() = _devicesList

    val text: LiveData<String> get() = _selectedDevice

    fun updateDevicesList(data: List<BluetoothDevice>) {
        _devicesList.value = data
    }

    fun updateText(text: String) {
        _selectedDevice.value = text
    }
}