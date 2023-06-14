package io.tripovan.voltage.data

class ScanResult {
    var cells: ArrayList<Double> = arrayListOf()
    var capacity: Double = 0.0
    var socRawHd: Double = 0.0
    var socDisplayed: Double = 0.0
}