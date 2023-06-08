package io.tripovan.voltage.ui.home

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class HomeViewModel : ViewModel() {

    private val _myData = MutableLiveData<List<BluetoothDevice>>()

    val myData: LiveData<List<BluetoothDevice>>
        get() = _myData

    fun updateData(data: List<BluetoothDevice>) {
        _myData.value = data
    }
}