package io.tripovan.voltage.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.tripovan.voltage.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class DashboardFragment : Fragment() {
    val cellValues = arrayOf(4.086, 4.086, 4.084, 4.089, 4.085, 4.087, 4.091, 4.088, 4.092, 4.091, 4.091, 4.092, 4.091, 4.092, 4.09, 4.092, 4.087, 4.089, 4.091, 4.089, 4.089, 4.091, 4.089, 4.092, 4.091, 4.09, 4.09, 4.091, 4.088, 4.086, 4.09, 4.086, 4.086, 4.091, 4.089, 4.085, 4.09, 4.09, 4.09, 4.091, 4.087, 4.089, 4.09, 4.091, 4.092, 4.091, 4.091, 4.091, 4.091, 4.091, 4.094, 4.09, 4.091, 4.095, 4.095, 4.094, 4.094, 4.091, 4.094, 4.094, 4.094, 4.096, 4.088, 4.091, 4.09, 4.09, 4.09, 4.091, 4.089, 4.087, 4.089, 4.092, 4.092, 4.091, 4.092, 4.094, 4.094, 4.092, 4.094, 4.089, 4.091, 4.091, 4.091, 4.091, 4.092, 4.091, 4.092, 4.097, 4.092, 4.094, 4.094, 4.094, 4.095, 4.094, 4.092)
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Create the BarChart
        val barChart: BarChart = binding.barChart;

        // Create data entries for the BarChart
        val entries = mutableListOf<BarEntry>()
        for (index in cellValues.indices) {
            entries.add(BarEntry(index.toFloat(), cellValues[index].toFloat()))
        }


        // Create a BarDataSet with the entries
        val dataSet = BarDataSet(entries, "BarDataSet")
        dataSet.color = Color.BLUE

        // Create a BarData object with the dataSet
        val data = BarData(dataSet)
        data.barWidth = 0.9f

        // Set the data to the BarChart
        barChart.data = data

        // Customize the appearance of the BarChart
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.xAxis.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false


        // Refresh the BarChart
        barChart.invalidate()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}