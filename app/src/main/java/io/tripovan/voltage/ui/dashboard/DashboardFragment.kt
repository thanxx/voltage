package io.tripovan.voltage.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.tripovan.voltage.App
import io.tripovan.voltage.R
import io.tripovan.voltage.communication.BluetoothManager
import io.tripovan.voltage.data.ScanResult
import io.tripovan.voltage.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!
    private var bluetooth: BluetoothManager? = null

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
        try {
            var bluetoothManager = App.instance.getBluetoothManager()
            if (bluetoothManager != null) {
                bluetooth = App.instance.getBluetoothManager()
            }
        } catch (e: Exception) {
            Toast.makeText(App.instance.applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }

        if (adapterAddress == null) {
            button.text = "Select OBD2 adapter"
            button.setOnClickListener {
                val navView: BottomNavigationView =
                    requireActivity().findViewById(R.id.nav_view)
                navView.selectedItemId = R.id.navigation_settings
            }
        }
        if (bluetooth == null) {
            button.isEnabled = false
            Toast.makeText(context, "Enable bluetooth and try again", Toast.LENGTH_SHORT).show()
        } else {
            button.text = "Scan"
            val spinner = requireActivity().findViewById<View>(R.id.loadingPanel) as ProgressBar
            spinner.visibility = View.GONE
            button.setOnClickListener {
                spinner.visibility = View.VISIBLE


                GlobalScope.launch {

                    var scan = ScanResult()
                    // Perform work in the background
                    try {
                        scan = bluetooth?.scan()!!
                    } catch (e: Exception) {
                        e.message?.let { it1 -> App.instance.showToast(it1) }
                    } finally {
                        bluetooth?.bluetoothSocket?.close()
                    }

                    // Update UI or perform other operations with the result
                    withContext(Dispatchers.Main) {
                        if (scan.cells.isNotEmpty()) {
                            dashboardViewModel.updateCells(scan.cells)
                        }
                        spinner.visibility = View.GONE
                    }
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
