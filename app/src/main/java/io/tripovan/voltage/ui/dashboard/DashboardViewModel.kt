package io.tripovan.voltage.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.tripovan.voltage.data.ScanResultEntry

class DashboardViewModel : ViewModel() {

    private val _scan = MutableLiveData<ScanResultEntry>()
    private val _summary = MutableLiveData<String>()
    private val _cellsSummary = MutableLiveData<String>()
    private val _spread = MutableLiveData<Double>()
    private val _selectedCell = MutableLiveData<String>()

    init {
        clearSelectedCell()
    }

    val cells: LiveData<ScanResultEntry> get() = _scan

    val summary: LiveData<String> get() = _summary

    val cellsSummary: LiveData<String> get() = _cellsSummary

    val spread: LiveData<Double> get() = _spread

    val selectedCell: LiveData<String> get() = _selectedCell


    fun updateScan(data: ScanResultEntry) {
        _scan.value = data
    }

    fun updateSummary(summary: String) {
        _summary.value = summary
    }

    fun updateCellsSummary(summary: String) {
        _cellsSummary.value = summary
    }

    fun updateSpread(spread: Double) {
        _spread.value = spread
    }

    fun updateSelectedCell(cell: String) {
        _selectedCell.postValue(cell)
    }

    fun clearSelectedCell() {
        _selectedCell.value = "Select cell to highlight"
    }
}