package io.tripovan.voltage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ScanResults")
class ScanResultEntry(
    @PrimaryKey
    val timestamp: Long,
    val vin: String,
    val odometer: Long,
    val cells: DoubleArray,
    val capacity: Double,
    val socRawHd: Double,
    val socDisplayed: Double,
    val minCell: Double,
    val maxCell: Double,
    val avgCell: Double,
    val cellSpread: Double
)
