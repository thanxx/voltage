package io.tripovan.voltage.obd2

import io.tripovan.voltage.data.ScanResult
import android.util.Log
import io.tripovan.voltage.App
import java.math.BigInteger

class Volt2Obd2Impl : VehicleScanResultsProvider {

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

    fun getCellsVoltages(): ArrayList<Double> {
        val cells = ArrayList<Double>()
        for (i in 0..95) {

            val cellResponse = App.socketManager!!.readObd(cellPids[i] + "1" + "\r\n")
            val arr = cellResponse.split(" ")
            val a = BigInteger(arr[arr.size - 2], 16).toDouble()
            val b = BigInteger(arr[arr.size - 1], 16).toDouble()
            val voltage = (((a * 256) + b) * 5) / 65535
            cells.add(voltage)
            Log.i("BT", voltage.toString())
        }
        return cells
    }

    fun getSocRawHd(): Double {
        TODO("Not yet implemented")
    }

    fun getSocRawDisplayed(): Double {
        TODO("Not yet implemented")
    }

    fun getCapacity(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun scan(): ScanResult{
        var scan = ScanResult()

        scan.cells = getCellsVoltages()

        return scan
    }


//
//    def get_soc_raw_hd(sock):
//    resp = get_obd("2243AF", sock).split()
//    soc = ((int(resp[-2],16) * 256) + int(resp[-1], 16)) * 100 / 65535
//    return soc
//
//
//    def get_battery_capacity(sock):
//    resp = get_obd("2241A3", sock).split()
//    cap = int((resp[-2]+resp[-1]), 16) / 30
//    return cap
//
//
//    def get_soc_displayed(sock):
//    resp = get_obd("228334", sock).split()
//    level = int(resp[-1], 16) * 100 / 255
//    return level
}