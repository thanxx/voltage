package io.tripovan.voltage.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.tripovan.voltage.R
import io.tripovan.voltage.bluetooth.BluetoothManager
import io.tripovan.voltage.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

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
        dashboardViewModel.summary.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val sharedPref = context?.getSharedPreferences("voltage_settings", Context.MODE_PRIVATE)
        val adapterAddress = sharedPref?.getString("adapter_address", null)

        val button = binding.scan
        if (adapterAddress == null) {
            button.text = "Select OBD2 adapter"
            button.setOnClickListener {
                val navView: BottomNavigationView =
                    requireActivity().findViewById(R.id.nav_view)
                navView.selectedItemId = R.id.navigation_home
            }
        } else {
            button.text = "Scan"
            button.setOnClickListener {
                try {
                    dashboardViewModel.updateCells(
                        BluetoothManager(adapterAddress).scan().get("cell_voltages") as List<Double>
                    )
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Create the BarChart
        val barChart: BarChart = binding.barChart
        dashboardViewModel.cells.observe(viewLifecycleOwner) {
            val entries = mutableListOf<BarEntry>()
            for (index in it.indices) {
                entries.add(BarEntry(index.toFloat(), it[index].toFloat()))
            }
            val dataSet = BarDataSet(entries, "BarDataSet")
            dataSet.color = Color.BLUE

            // Create a BarData object with the dataSet
            val data = BarData(dataSet)
            data.barWidth = 0.9f

            // Set the data to the BarChart
            barChart.data = data

            // Customize the appearance of the BarChart
            barChart.setFitBars(true)
            barChart.setDrawValueAboveBar(true)
            barChart.description.isEnabled = false
            barChart.xAxis.isEnabled = false
            barChart.axisRight.isEnabled = false
            barChart.legend.isEnabled = false

            // Refresh the BarChart
            barChart.invalidate()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}