package io.tripovan.voltage.ui.history

import android.content.res.Resources
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import io.tripovan.voltage.App
import io.tripovan.voltage.data.DateXAxisFormatter
import io.tripovan.voltage.databinding.FragmentHistoryBinding
import io.tripovan.voltage.ui.dashboard.DashboardViewModel
import io.tripovan.voltage.utils.Constants
import io.tripovan.voltage.utils.TimestampReducer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class HistoryFragment : Fragment(), OnChartValueSelectedListener {

    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!

    private lateinit var capacityChart: LineChart
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
            textView.text = "Capacity: $it"
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

        capacityChart = binding.capacityChart
        capacityChart.xAxis.valueFormatter = DateXAxisFormatter()
        capacityChart.xAxis.gridColor = textColor //TODO make more soft
        capacityChart.xAxis.textColor = textColor
        capacityChart.xAxis.axisLineColor = textColor
        capacityChart.legend.textColor = textColor
        capacityChart.axisLeft.textColor = textColor
        capacityChart.axisLeft.axisLineColor = textColor
        capacityChart.axisRight.axisLineColor = textColor
        capacityChart.axisRight.textColor = textColor
        capacityChart.setDrawMarkers(true)
        capacityChart.setOnChartValueSelectedListener(this)

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
                capacityChart.data = lineData


                capacityChart.invalidate()

            }
        }


        return root
    }


    override fun onValueSelected(e: Entry, h: Highlight?) {
        capacityChart.highlightValue(h)
        var longDate = TimestampReducer.floatToLongTs(e.x)

        val date = Date(longDate)

        historyViewModel.updateCapacityText("%.2f KWh\n$date".format(e.y))
        App.currentTimestamp = longDate
        dashboardViewModel.clearSelectedCell()
    }

    override fun onNothingSelected() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}