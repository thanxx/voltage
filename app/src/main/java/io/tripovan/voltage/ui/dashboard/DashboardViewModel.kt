package io.tripovan.voltage.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _cells = MutableLiveData<List<Double>>()
    private val _summary = MutableLiveData<String>()

    val cells: LiveData<List<Double>>
        get() = _cells

    val summary: LiveData<String> get() = _summary

    fun updateCells(data: List<Double>) {
        _cells.value = data
    }

    fun updateSummary(summary: String) {
        _summary.value = "$summary"
    }
}