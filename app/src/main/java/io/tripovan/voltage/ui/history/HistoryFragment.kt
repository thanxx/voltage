package io.tripovan.voltage.ui.history

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import io.tripovan.voltage.App
import io.tripovan.voltage.data.DateXAxisFormatter
import io.tripovan.voltage.databinding.FragmentHistoryBinding
import io.tripovan.voltage.ui.dashboard.DashboardViewModel
import io.tripovan.voltage.utils.TimestampReducer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class HistoryFragment : Fragment(), OnChartValueSelectedListener {

    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!

    private lateinit var historyChart: LineChart
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        historyViewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

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



        GlobalScope.launch {
            var historyData = App.database.scanResultDao().getAllScanResults()


            withContext(Dispatchers.Main) {
                historyViewModel.updateHistory(historyData)
            }
        }

        val textColor = typedValue.data
        theme.resolveAttribute(androidx.transition.R.attr.colorPrimary, typedValue, true)
        val lineColor = typedValue.data
        val gridcolor = ContextCompat.getColor(requireContext(), com.google.android.material.R.color.material_grey_600);
        historyChart = binding.capacityChart
        historyChart.xAxis.valueFormatter = DateXAxisFormatter()
        historyChart.xAxis.gridColor = gridcolor
        historyChart.axisRight.gridColor = gridcolor
        historyChart.axisLeft.gridColor = gridcolor
        historyChart.xAxis.textColor = textColor
        historyChart.xAxis.axisLineColor = textColor
        historyChart.legend.textColor = textColor
        historyChart.axisLeft.textColor = textColor
        historyChart.axisLeft.axisLineColor = textColor
        historyChart.axisRight.axisLineColor = textColor
        historyChart.axisRight.textColor = textColor
        historyChart.setDrawMarkers(true)
        historyChart.setOnChartValueSelectedListener(this)

        historyViewModel.historyData.observe(viewLifecycleOwner){ it ->
            var capacityTimeSeries = ArrayList<Entry>()
            it.forEach {
                capacityTimeSeries.add(
                    Entry(
                        TimestampReducer.longToFloatTs(it.timestamp),
                        it.capacity.toFloat()
                    )
                )
                val lineDataSet = LineDataSet(capacityTimeSeries, "Capacity, KWh")
                lineDataSet.setDrawValues(false)
                lineDataSet.color = lineColor
                lineDataSet.setDrawHighlightIndicators(true)
                lineDataSet.isHighlightEnabled = true
                val lineData = LineData()
                lineData.addDataSet(lineDataSet)
                lineData.setValueTextColor(textColor)
                historyChart.data = lineData
                historyChart.description.text = ""
                historyChart.invalidate()
            }
        }
        return root
    }


    override fun onValueSelected(e: Entry, h: Highlight?) {
        historyChart.highlightValue(h)
        var longDate = TimestampReducer.floatToLongTs(e.x)
        val date = Date(longDate)
        historyViewModel.updateText("$date\nCapacity: %.2f KWh".format(e.y))
        App.currentTimestamp = longDate
        dashboardViewModel.clearSelectedCell()
    }

    override fun onNothingSelected() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}