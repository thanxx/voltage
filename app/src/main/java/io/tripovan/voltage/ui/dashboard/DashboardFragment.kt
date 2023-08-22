package io.tripovan.voltage.ui.dashboard

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.tripovan.voltage.App
import io.tripovan.voltage.R
import io.tripovan.voltage.communication.SocketManager
import io.tripovan.voltage.communication.obd2.Volt2Obd2Impl
import io.tripovan.voltage.data.ScanResultEntry
import io.tripovan.voltage.databinding.FragmentDashboardBinding
import io.tripovan.voltage.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class DashboardFragment : Fragment(),
    OnChartValueSelectedListener {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var socketManager: SocketManager? = null
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val textView: TextView = binding.textDashboard
        val cellsSummary: TextView = binding.cellsSummary
        val spreadTextView: TextView = binding.spread
        val selectedCell: TextView = binding.selectedCell

        dashboardViewModel.summary.observe(viewLifecycleOwner) {
            textView.text = it
        }
        dashboardViewModel.cellsSummary.observe(viewLifecycleOwner) {
            cellsSummary.text = it
        }
        dashboardViewModel.spread.observe(viewLifecycleOwner) {
            spreadTextView.text = String.format("Disbalance: %.1f mV", it * 1000)
            spreadTextView.setTextColor(Color.GREEN)
            if (it > 0.120) {
                spreadTextView.setTextColor(Color.RED)
            }
            if (it > 0.90) {
                spreadTextView.setTextColor(Color.YELLOW)
            }
        }
        dashboardViewModel.selectedCell.observe(viewLifecycleOwner) {
            selectedCell.text = it
        }

        val sharedPref = App.instance.getSharedPrefs()
        val adapterAddress = sharedPref?.getString("adapter_address", null)

        val button = binding.scan
        try {
            val bluetoothManager = App.instance.getBluetoothManager()
            if (bluetoothManager != null) {
                socketManager = App.instance.getBluetoothManager()
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
        if (socketManager == null) {
            button.isEnabled = false
        } else {
            button.text = "Scan"


            button.setOnClickListener {
                val spinner = activity?.findViewById<View>(R.id.loadingPanel) as? ProgressBar

                GlobalScope.launch {

                    var scan: ScanResultEntry? = null
                    var retryCount = 3
                    while (retryCount > 0) {
                        withContext(Dispatchers.Main) {
                            spinner?.visibility = View.VISIBLE
                        }
                        try {
                            // todo select vehicle implementation depending on app settings
                            scan = Volt2Obd2Impl().scan()

                            if (scan.odometer > 0) {
                                retryCount = 0
                            }

                        } catch (e: Exception) {
                            e.message?.let { it1 -> App.instance.showToast(it1) }
                            retryCount--
                        }

                        withContext(Dispatchers.Main) {
                            updateUI(scan)
                            spinner?.visibility = View.GONE
                        }
                    }

                    if (scan != null) {
                        App.database.scanResultDao().insert(scan)
                    }
                }
            }
        }

        val typedValue = TypedValue()
        val theme: Resources.Theme = requireContext().theme
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSecondary,
            typedValue,
            true
        )
        val textColor = typedValue.data
        theme.resolveAttribute(R.attr.barchartColor, typedValue, true)
        val accentColor = typedValue.data


        val barChart: BarChart = binding.barChart
        dashboardViewModel.cells.observe(viewLifecycleOwner) {
            val entries = mutableListOf<BarEntry>()
            for (index in it.cells.indices) {
                entries.add(BarEntry(index.toFloat(), it.cells[index].toFloat()))
            }
            val dataSet = BarDataSet(entries, "BarDataSet")
            dataSet.color = accentColor

            val data = BarData(dataSet)
            data.barWidth = 0.3f

            barChart.data = data
            barChart.axisLeft.textColor = textColor

            barChart.setFitBars(false)
            barChart.setDrawValueAboveBar(false)

            barChart.setDrawMarkers(false)

            barChart.isAutoScaleMinMaxEnabled = false
            barChart.description.isEnabled = false
            barChart.xAxis.isEnabled = false
            barChart.axisRight.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.setMaxVisibleValueCount(1)
            barChart.setOnChartValueSelectedListener(this)

            barChart.invalidate()
        }

        GlobalScope.launch {

            var scan: ScanResultEntry? = null
            try {
                val results = App.database.scanResultDao().getAllScanResults()
                if (results.isNotEmpty()) {
                    scan = App.database.scanResultDao().getAllScanResults().last()
                }

            } catch (e: Exception) {
                e.message?.let { it1 -> App.instance.showToast(it1) }
            }

            if (scan != null) {
                withContext(Dispatchers.Main) {
                    updateUI(scan)
                }
            }
        }



        return root
    }


    private fun updateUI(scan: ScanResultEntry?) {
        if (scan != null) {
            val capacity = scan.capacity
            val socRawHd = scan.socRawHd
            val socDisplayed = scan.socDisplayed
            if (scan.cells.isNotEmpty()) {
                val sharedPref = App.instance.getSharedPrefs()
                val unitsList = resources.getStringArray(R.array.distance_units).toList()
                val units = sharedPref?.getString("distance_units", unitsList[0])

                val odometerText = if (units == unitsList[0]) {
                    "${scan.odometer} $units"
                } else {
                    String.format("%.2f $units", scan.odometer * 0.62137119)
                }

                dashboardViewModel.updateScan(scan)
                dashboardViewModel.updateSummary(
                    String.format(
                        "Date: %s \nOdometer: ~%s\nCapacity: %.3f KWh \\ %.2f%% \nSoC Raw HD: %.1f %%\nSoC Displayed: %.1f %%",
                        Date(scan.timestamp).toString(),
                        odometerText,
                        capacity,
                        capacity / Constants.volt2InitialCapacity * 100,
                        socRawHd,
                        socDisplayed
                    )
                )
                dashboardViewModel.updateCellsSummary(String.format(
                    "Min: %.3f V #%s\nAvg: %.3f V\nMax: %.3f V #%s",
                    scan.minCell,
                    scan.cells.indices.minBy { scan.cells[it] } + 1,
                    scan.avgCell,
                    scan.maxCell,
                    scan.cells.indices.maxBy { scan.cells[it] } + 1))
                dashboardViewModel.updateSpread(scan.cellSpread)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e != null) {
            val entry = e as BarEntry
            val cellNo = entry.x.toInt()

            var group = 1

            val section = when {
                cellNo > 84 -> 3.also { group = 7}
                cellNo > 72 -> 3.also { group = 6 }
                cellNo > 56 -> 2.also { group = 5 }
                cellNo > 44 -> 2.also { group = 4 }
                cellNo > 28 -> 1.also { group = 3 }
                cellNo > 16 -> 1.also { group = 2 }
                else -> 1
            }

            dashboardViewModel.updateSelectedCell(
                String.format(
                    "Selected cell: #%d, %.3f V, Section: %s, Group: %s",
                    cellNo + 1, entry.y, section, group
                )
            )
        }
    }

    override fun onNothingSelected() {
        dashboardViewModel.updateSelectedCell(" ")
    }


}
