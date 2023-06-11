package io.tripovan.voltage.bluetooth


import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.io.IOException
import java.util.UUID

class BluetoothService() : Service() {

    private val TAG = "BluetoothService"

    // UUID for RFCOMM
    private val UUID_RFCOMM: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // todo pass adapter from HomeFragment
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothSocket: BluetoothSocket

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth error")
            return
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(btDevice: BluetoothDevice) {

        if (btDevice != null) {
            try {
                // Create a Bluetooth socket using the RFCOMM UUID
                bluetoothSocket = btDevice.createInsecureRfcommSocketToServiceRecord(UUID_RFCOMM)

                // Connect to the device
                bluetoothSocket.connect()

                Log.d(TAG, "Connected to ${btDevice.name}")

                // TODO: Implement your data transmission/receiving logic here

            } catch (e: IOException) {
                Log.e(TAG, "Failed to connect to ${btDevice.name}: ${e.message}")
            }
        } else {
            Log.e(TAG, "Device not found")
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        // Disconnect from the Bluetooth device
        try {
            bluetoothSocket.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing Bluetooth socket: ${e.message}")
        }
    }
}