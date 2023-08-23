package io.tripovan.voltage.data

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import io.tripovan.voltage.utils.TimestampReducer
import java.text.SimpleDateFormat
import java.util.Date

class DateXAxisFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String? {

        val ts: Long = TimestampReducer.floatToLongTs(value)

        val date = Date(ts)
        val dateTimeFormat = SimpleDateFormat("dd-MM-yy")

        return dateTimeFormat.format(date)
    }
}