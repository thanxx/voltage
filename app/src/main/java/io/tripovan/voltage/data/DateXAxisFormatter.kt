package io.tripovan.voltage.data

import android.util.Log
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Date

class DateXAxisFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String? {

        val ts: Long = value.toLong()

        val date = Date(ts)
        val dateTimeFormat = SimpleDateFormat("yy-MM-dd")

        return dateTimeFormat.format(date)
    }
}