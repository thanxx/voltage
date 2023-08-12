package io.tripovan.voltage.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.tripovan.voltage.data.ScanResultEntry

class HistoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tap on chart to highlight value"
    }
    val text: LiveData<String> = _text

    private var _historyData = MutableLiveData<List<ScanResultEntry>>().apply {
        value = ArrayList<ScanResultEntry>()
    }
    var historyData: LiveData<List<ScanResultEntry>> = _historyData

    fun updateHistory(history: List<ScanResultEntry>){
        _historyData.value = history
    }

    fun updateCapacityText(text: String) {
        _text.value = text
    }
}