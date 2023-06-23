package io.tripovan.voltage.obd2

import android.util.Log
import io.tripovan.voltage.App
import io.tripovan.voltage.communication.SocketManager
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat

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

    fun decodeResponse(input: String):ArrayList<Double> {
        if (input.contains("UNABLE TO CONNECT")) {
            throw Obd2DecodeException("Turn on the vehicle! Can't connect to CAN bus")
        }
        try {
            val arr = input.split(" ")
            val a = BigInteger(arr[arr.size - 2], 16).toDouble()
            val b = BigInteger(arr[arr.size - 1], 16).toDouble()

            return arrayListOf(a, b)
        } catch (e: Exception) {
            throw java.lang.Exception("Can't decode input: $input, reason: ${e.message}")
        }
    }

//    fun roundDouble(input: Double): Double{
//        Log.i("OBD2", "Round input: $input")
//        val decimalFormat = DecimalFormat("#,###")
//        decimalFormat.roundingMode = RoundingMode.HALF_UP
//        val rounded = decimalFormat.format(input).toDouble()
//        Log.i("OBD2", "Round output: $rounded")
//        return rounded
//    }
}