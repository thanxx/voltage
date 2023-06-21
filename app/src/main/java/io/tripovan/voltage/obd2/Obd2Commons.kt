package io.tripovan.voltage.obd2

import io.tripovan.voltage.App
import io.tripovan.voltage.communication.SocketManager

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
}