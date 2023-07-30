package io.tripovan.voltage.communication.obd2

import io.tripovan.voltage.App
import io.tripovan.voltage.communication.SocketManager
import java.math.BigInteger

open class Obd2Commons {
    private val socketManager: SocketManager = App.socketManager!!


    companion object {
        const val obdAtz = "ATZ \r\n"
        const val obdAte = "ATE0 \r\n"
        const val obdAtsp = "ATSP0 \r\n"
    }

    fun initObd() {
        if (socketManager.bluetoothSocket.isConnected) {
            socketManager.readObd(obdAtz)
            //todo catch response and indicate OBD2 connection ???
            socketManager.readObd(obdAte)
            socketManager.readObd(obdAtsp)
        }
    }

    fun decodeResponse(input: String, size: Int):ArrayList<Int> {
        if (input.contains("UNABLE TO CONNECT") || input.contains("NO DATA")) {
            throw Obd2DecodeException("Most likely, the vehicle is turned off. Turn it on to read data")
        }
        try {
            val arr = input.split(" ")
            var outputArray = ArrayList<Int>()

            for (i in size + 1 downTo 0) {
                outputArray.add(arr[arr.size - i - 1].toInt(16))
            }

            return outputArray
        } catch (e: Exception) {
            throw java.lang.Exception("Can't decode input: $input, reason: ${e.message}")
        }
    }
}