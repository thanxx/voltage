package io.tripovan.voltage

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.room.Room
import io.tripovan.voltage.communication.SocketManager
import io.tripovan.voltage.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
        var socketManager: SocketManager? = null
        lateinit var database: AppDatabase

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
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "voltageDB1"
        )
            .build()
    }

    override fun onTerminate() {
        super.onTerminate()
        instance.getBluetoothManager()?.bluetoothSocket?.close()
    }

    fun initBluetooth(address: String) {
        socketManager = SocketManager(address)
    }

    fun getBluetoothManager(): SocketManager? {
        return socketManager
    }

    suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(instance.applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }


}