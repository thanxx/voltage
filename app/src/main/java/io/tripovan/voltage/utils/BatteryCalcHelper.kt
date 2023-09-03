package io.tripovan.voltage.utils


import io.tripovan.voltage.App.Companion.voltModel

class BatteryInfo(val actualAh: Double) {


        fun getActualWattHours(): Double {
            return voltModel.voltageNominal  * actualAh
        }

        fun getCapacityPercent() : Double {
            return getActualWattHours() / voltModel.getNominalWh() * 100}
}