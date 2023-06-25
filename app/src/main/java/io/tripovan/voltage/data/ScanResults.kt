package io.tripovan.voltage.data

import androidx.room.PrimaryKey
import java.sql.Timestamp

data class ScanResults(
    @PrimaryKey
    val timestamp: Timestamp,
    val vin: String,
    val scanResult: ScanResult)