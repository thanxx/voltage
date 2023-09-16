package io.tripovan.voltage.communication.obd2

import io.tripovan.voltage.App
import io.tripovan.voltage.communication.SocketManager

open class Obd2Commons {
    private val socketManager: SocketManager = App.socketManager!!

    fun initObd() {
        socketManager.readObd("ATZ \r\n")
        socketManager.readObd("ATE0 \r\n")
        socketManager.readObd("ATSP0 \r\n")
        socketManager.readObd("ATL0 \r\n")
        socketManager.readObd("ATS1 \r\n")
    }

    fun decodeResponse(input: String, size: Int): ArrayList<Int> {
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