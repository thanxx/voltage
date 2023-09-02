package io.tripovan.voltage

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.Room
import io.tripovan.voltage.communication.SocketManager
import io.tripovan.voltage.data.AppDatabase
import io.tripovan.voltage.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class App : Application() {
    companion object {

        var currentTimestamp: Long? = null
        lateinit var instance: App
            private set
        var socketManager: SocketManager? = null
        lateinit var database: AppDatabase
        lateinit var appVersion: String
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "voltageDB1"
        ).build()

        val packageManager = this.packageManager
        val packageName = this.packageName
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val versionCode: Long =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, 0).longVersionCode
            } else {
                packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
            }

        appVersion = "Version $versionName ($versionCode)"
        Log.i(Constants.TAG, "Starting Voltage app, $appVersion")
    }

    override fun onTerminate() {
        super.onTerminate()
        instance.getBluetoothManager()?.bluetoothSocket?.close()
    }

    fun initBluetooth(address: String) {
        socketManager = SocketManager(address)
    }

    fun getBluetoothManager(): SocketManager? {
        val sharedPref = getSharedPrefs()
        val adapterAddress = sharedPref?.getString("adapter_address", null)
        if (adapterAddress != null) {
            try {
                instance.initBluetooth(adapterAddress)
            } catch (e: Exception) {
                GlobalScope.launch { e.message?.let { showToast(it) } }
            }
        }
        return socketManager
    }

    suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(instance.applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    fun getSharedPrefs() : SharedPreferences? {
        return this.getSharedPreferences("voltage_settings", Context.MODE_PRIVATE)
    }

    fun isBtPermissionEnabled():Boolean {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
    }
}