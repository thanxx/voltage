package io.tripovan.voltage.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScanResultDao {
    @Insert
    suspend fun insert(entry: ScanResultEntry)

    @Query("SELECT * FROM ScanResults")
    suspend fun getAllScanResults(): List<ScanResultEntry>

//    @Query("SELECT * FROM ScanResults WHERE vin='%s'")
//    suspend fun getAllScanResultsByVin(vin: String): List<ScanResultEntry>
}