package io.tripovan.voltage.data
import androidx.room.TypeConverter

class TypeConverter {
    @TypeConverter
    fun fromDoubleArray(doubleArray: DoubleArray): String {
        return doubleArray.joinToString(",")
    }

    @TypeConverter
    fun toDoubleArray(doubleArrayString: String): DoubleArray {
        return doubleArrayString.split(",").map { it.toDouble() }.toDoubleArray()
    }
}