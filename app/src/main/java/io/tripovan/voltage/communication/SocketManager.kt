package io.tripovan.voltage.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import io.tripovan.voltage.data.ScanResult
import io.tripovan.voltage.obd2.Volt2Obd2Impl
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class SocketManager constructor(private val address: String) {

    private var inputStream: InputStream
    private var outputStream: OutputStream


    private var device: BluetoothDevice? = null
    var bluetoothSocket: BluetoothSocket

    companion object {

        private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        @SuppressLint("MissingPermission")
        fun getPairedDevices(): List<BluetoothDevice> {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
            val deviceList = mutableListOf<BluetoothDevice>()
            pairedDevices?.let {
                for (device: BluetoothDevice in it) {
                    deviceList.add(device)
                }
            }
            return deviceList
        }
    }

    init {
        getSocket().let {
            if (it != null) {
                bluetoothSocket = it
                outputStream = bluetoothSocket.outputStream
                inputStream = bluetoothSocket.inputStream
            } else {
                throw Exception("Can't initialize bluetooth socket")
            }
        }
        Log.i("BT", "Socket initialized")
    }



    @SuppressLint("MissingPermission")
    private fun getSocket(): BluetoothSocket? {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        if (!pairedDevices.isNullOrEmpty()) {
            device = pairedDevices.find { device ->
                device.address == address
            }
            device.let {
                return device?.createRfcommSocketToServiceRecord(
                    device?.uuids?.get(0)?.uuid
                )
            }
        } else {
            throw Exception("Bluetooth error, enable it, check app settings...")
        }
    }


    @SuppressLint("MissingPermission")
    fun readObd(cmd: String): String {
        if (!bluetoothSocket.isConnected) {
            bluetoothSocket.connect()
        }

        var data = ""

        // Send
        try {
            Log.i("BT SEND", cmd)
            outputStream.write(cmd.toByteArray())
        } catch (e: Exception) {
            e.message?.let {
                Log.e("BT SEND", it)
                throw RuntimeException("Error sending data")
            }
        }

        // Receive
        try {
            val terminationSymbol = '>'.code.toByte()
            var byteRead: Int
            val buffer = ByteArray(1)


            // Read data byte by byte until the termination symbol is encountered
            while (inputStream.read(buffer).also { byteRead = it } != -1) {
                val receivedByte = buffer[0]

                if (receivedByte == terminationSymbol) {
                    break
                }
                data += buffer.decodeToString()
                //Log.d("BT RECV", data)

            }

        } catch (e: IOException) {
            e.message?.let { Log.e("BT RECV", it) }
            throw Exception(e.message)
        }

        data = data.trim()
        data = data.replace("\n", "")
        data = data.replace("\r", "")
        data = data.replace("SEARCHING...", "")
        Log.i("BT RECV DATA: ", data)
        return data
    }
}