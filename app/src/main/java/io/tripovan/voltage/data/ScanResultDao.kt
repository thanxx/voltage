package io.tripovan.voltage.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.sql.Timestamp

@Dao
interface ScanResultDao {
    @Insert
    suspend fun insert(entry: ScanResultEntry)

    @Query("SELECT * FROM ScanResults")
    suspend fun getAllScanResults(): List<ScanResultEntry>

    @Query("SELECT * FROM ScanResults WHERE timestamp == :timestamp LIMIT 1")
    suspend fun getScanResultByTimestamp(timestamp: Long): ScanResultEntry



//    @Query("SELECT * FROM ScanResults WHERE vin==:vin")
//    suspend fun getAllScanResultsByVin(vin: String): List<ScanResultEntry>
}