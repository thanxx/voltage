package io.tripovan.voltage.utils

object Constants {
    const val TAG = "Voltage"
    const val volt2InitialCapacity = 18.4

    val largeNumberToExtractFromTs = 1690000000000

    val permissions = arrayOf(
        "android.permission.BLUETOOTH",
        "android.permission.BLUETOOTH_ADMIN",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.BLUETOOTH_SCAN",
        "android.permission.WRITE_EXTERNAL_STORAGE")
}