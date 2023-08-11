package io.tripovan.voltage.ui.history

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.tripovan.voltage.App
import io.tripovan.voltage.R
import io.tripovan.voltage.data.DateXAxisFormatter
import io.tripovan.voltage.databinding.FragmentHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val historyViewModel =
            ViewModelProvider(this)[HistoryViewModel::class.java]

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHistory
        historyViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val typedValue = TypedValue()
        val theme: Resources.Theme = requireContext().theme
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSecondary,
            typedValue,
            true
        )
        val textColor = typedValue.data
        theme.resolveAttribute(androidx.transition.R.attr.colorPrimary, typedValue, true)
        val lineColor = typedValue.data

        val capacityChart = binding.capacityChart
        GlobalScope.launch {
            val scans = App.database.scanResultDao().getAllScanResults()
            var capacityTimeSeries = ArrayList<Entry>()
            scans.forEach {capacityTimeSeries.add(Entry(it.timestamp.toFloat(), it.capacity.toFloat()))}
            withContext(Dispatchers.Main) {
                capacityChart.xAxis.valueFormatter = DateXAxisFormatter()

                val lineData = LineData()
                val lineDataSet = LineDataSet(capacityTimeSeries, "Capacity, KWh")
                lineDataSet.setDrawValues(false)
                lineDataSet.color = lineColor

                lineData.addDataSet(lineDataSet)
                lineData.setValueTextColor(textColor)
                capacityChart.data = lineData
                capacityChart.xAxis.gridColor = textColor
                capacityChart.xAxis.textColor = textColor
                capacityChart.xAxis.axisLineColor = textColor
                capacityChart.legend.textColor = textColor
                capacityChart.axisLeft.textColor = textColor
                capacityChart.axisLeft.axisLineColor = textColor
                capacityChart.axisRight.axisLineColor = textColor
                capacityChart.axisRight.textColor = textColor


                capacityChart.invalidate()
            }
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}