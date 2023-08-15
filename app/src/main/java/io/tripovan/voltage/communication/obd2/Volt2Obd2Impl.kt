package io.tripovan.voltage.communication.obd2

import android.util.Log
import io.tripovan.voltage.App
import io.tripovan.voltage.data.ScanResultEntry
import io.tripovan.voltage.utils.Constants
import java.math.BigInteger
import kotlin.math.pow

class Volt2Obd2Impl : VehicleScanResultsProvider, Obd2Commons() {

    private val TAG = Constants.TAG

    private val cellPids = arrayOf(
        "224181",
        "224182",
        "224183",
        "224184",
        "224185",
        "224186",
        "224187",
        "224188",
        "224189",
        "22418A",
        "22418B",
        "22418C",
        "22418D",
        "22418E",
        "22418F",
        "224190",
        "224191",
        "224192",
        "224193",
        "224194",
        "224195",
        "224196",
        "224197",
        "224198",
        "224199",
        "22419A",
        "22419B",
        "22419C",
        "22419D",
        "22419E",
        "22419F",
        "224200",
        "224201",
        "224202",
        "224203",
        "224204",
        "224205",
        "224206",
        "224207",
        "224208",
        "224209",
        "22420A",
        "22420B",
        "22420C",
        "22420D",
        "22420E",
        "22420F",
        "224210",
        "224211",
        "224212",
        "224213",
        "224214",
        "224215",
        "224216",
        "224217",
        "224218",
        "224219",
        "22421A",
        "22421B",
        "22421C",
        "22421D",
        "22421E",
        "22421F",
        "224220",
        "224221",
        "224222",
        "224223",
        "224224",
        "224225",
        "224226",
        "224227",
        "224228",
        "224229",
        "22422A",
        "22422B",
        "22422C",
        "22422D",
        "22422E",
        "22422F",
        "224230",
        "224231",
        "224232",
        "224233",
        "224234",
        "224235",
        "224236",
        "224237",
        "224238",
        "224239",
        "22423A",
        "22423B",
        "22423C",
        "22423D",
        "22423E",
        "22423F",
        "224240"
    )

    private fun getCellsVoltages(): ArrayList<Double> {

        val cells = ArrayList<Double>()
        for (i in 0..95) {

            val cellResponse = App.socketManager!!.readObd(cellPids[i] + "1" + "\r\n")
            val decoded = decodeResponse(cellResponse, 2)
            val voltage = ((
                    (decoded[decoded.size - 2].toDouble() * 256)
                            + decoded[decoded.size - 1].toDouble()) * 5) / 65535
            cells.add(voltage)
        }
        return cells
    }

    private fun getSocRawHd(): Double {
        val response = App.socketManager!!.readObd("2243AF1" + "\r\n")
        val decoded = decodeResponse(response, 2)
        return ((decoded[decoded.size - 2].toDouble() * 256) + decoded[decoded.size - 1].toDouble()) * 100 / 65535
    }

    private fun getSocRawDisplayed(): Double {
        val response = App.socketManager!!.readObd("228334" + "\r\n")
        val decoded = decodeResponse(response, 2)
        return decoded[decoded.size - 1].toDouble() * 100 / 255
    }

    private fun getCapacity(): Double {
        val response = App.socketManager!!.readObd("2241A31" + "\r\n")
        val arr = response.split(" ")
        return (BigInteger(arr[arr.size - 2] + arr[arr.size - 1], 16).toDouble()) / 30
    }

    private fun getVin(): String {
        val response = App.socketManager!!.readObd("0902" + "\r\n")
        val cut1 = response.split("49 02 01 ")[1]
        val cut2 = cut1.replace(Regex("[0-9]:\\s?"), "")
        val arr = cut2.split(" ")

        var vin = ""
        arr.forEach {
            vin += it.toInt(16).toChar()
        }
        return vin
    }

    private fun getOdometer(): Int {
        val arr = decodeResponse(App.socketManager!!.readObd("2234B2" + "\r\n"), 4)
        val a = arr[arr.size - 4]
        val b = arr[arr.size - 3]
        val c = arr[arr.size - 2]
        val d = arr[arr.size - 1]
        return (((a * 2.0.pow(24)) + (b * 2.0.pow(16)) + (c * 2.0.pow(8)) + d) / 64).toInt()
    }

    override suspend fun scan(): ScanResultEntry {
        initObd()
        var vin = ""
        var odometer = 0

        try {
            vin = getVin()
            odometer = getOdometer()
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }

        App.socketManager?.readObd("ATSH7E7 \r\n")

        val voltages = getCellsVoltages().toDoubleArray()
        App.socketManager?.readObd("ATSH7E4 \r\n")
        val capacity = getCapacity()
        val socRawHd = getSocRawHd()
        val socDisplayed = getSocRawDisplayed()

        val min = voltages.min()
        val max = voltages.max()
        val avg = voltages.average()
        val spread = max - min



        return ScanResultEntry(
            cells = voltages,
            maxCell = max,
            minCell = min,
            avgCell = avg,
            cellSpread = spread,
            socDisplayed = socDisplayed,
            socRawHd = socRawHd,
            capacity = capacity,
            odometer = odometer.toLong(),
            vin = vin,
            timestamp = System.currentTimeMillis()
        )
    }
}