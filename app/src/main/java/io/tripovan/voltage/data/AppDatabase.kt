package io.tripovan.voltage.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ScanResultEntry::class], version = 2, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanResultDao(): ScanResultDao
}