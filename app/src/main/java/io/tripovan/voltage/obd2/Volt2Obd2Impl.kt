package io.tripovan.voltage.obd2

import io.tripovan.voltage.data.ScanResult
import android.util.Log
import io.tripovan.voltage.App
import java.math.BigInteger

class Volt2Obd2Impl : VehicleScanResultsProvider, Obd2Commons() {

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
            val decoded = decodeResponse(cellResponse)
            val voltage = (((decoded[decoded.size - 2] * 256) + decoded[decoded.size - 1]) * 5) / 65535
            cells.add(voltage)
            Log.i("BT", voltage.toString())
        }
        return cells
    }

    private fun getSocRawHd(): Double {
        val response = App.socketManager!!.readObd( "2243AF1" + "\r\n")
        val decoded = decodeResponse(response)
        return ((decoded[decoded.size - 2] * 256) + decoded[decoded.size - 1]) * 100 / 65535


    }

    private fun getSocRawDisplayed(): Double {
        val response = App.socketManager!!.readObd( "228334" + "\r\n")
        val decoded = decodeResponse(response)
        return decoded[decoded.size - 1] * 100 / 255

    }

    private fun getCapacity(): Double {
        val response = App.socketManager!!.readObd( "2241A31" + "\r\n")
        val arr = response.split(" ")

        return (BigInteger(arr[arr.size - 2] + arr[arr.size - 1], 16).toDouble()) / 30

    }

    override suspend fun scan(): ScanResult{
        initObd()
        var scan = ScanResult()
        App.socketManager?.readObd("ATSH7E7 \r\n")
        scan.cells = getCellsVoltages()
        App.socketManager?.readObd("ATSH7E4 \r\n")
        scan.capacity = getCapacity()
        scan.socRawHd = getSocRawHd()
        scan.socDisplayed = getSocRawDisplayed()
        return scan
    }


}