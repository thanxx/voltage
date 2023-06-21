package io.tripovan.voltage.obd2

import io.tripovan.voltage.data.ScanResult

interface VehicleScanResultsProvider {
    suspend fun scan(): ScanResult
}