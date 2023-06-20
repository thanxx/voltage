package io.tripovan.voltage

import android.app.Application
import android.content.Context
import android.widget.Toast
import io.tripovan.voltage.communication.BluetoothManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
        var bluetoothManager: BluetoothManager? = null

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val sharedPref = getSharedPreferences("voltage_settings", Context.MODE_PRIVATE)
        val adapterAddress = sharedPref?.getString("adapter_address", null)
        if (adapterAddress != null) {
            instance.initBluetooth(adapterAddress)
        }

    }

    fun initBluetooth(address: String){
        bluetoothManager = BluetoothManager(address)
    }

    fun getBluetoothManager(): BluetoothManager? {
        return bluetoothManager
    }

    suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(instance.applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }
}