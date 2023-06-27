package io.tripovan.voltage.communication.obd2

import io.tripovan.voltage.data.ScanResultEntry

interface VehicleScanResultsProvider {
    suspend fun scan(): ScanResultEntry
}