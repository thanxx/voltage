package io.tripovan.voltage.utils

object Constants {
    const val TAG = "Voltage"

    const val largeNumberToExtractFromTs = 1690000000000

    abstract class VoltModel(ahNominal: Double, voltageNominal: Double) {
        var ahNominal: Double = ahNominal
        var voltageNominal: Double = voltageNominal
        fun getNominalWh(): Double {
            return ahNominal * voltageNominal
        }
    }

    object Volt20162019 : VoltModel(51.8, 355.2)
    object Volt2015 : VoltModel(50.2, 340.8)
    object Volt20132014 : VoltModel(48.0, 345.6)
    object Volt20112012 : VoltModel(45.0, 355.2)
}