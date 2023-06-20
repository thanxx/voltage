package io.tripovan.voltage

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesChangeListener(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val listener: () -> Unit
) : SharedPreferences.OnSharedPreferenceChangeListener {

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // Check if the specific key you are interested in has changed,
        // or perform your desired logic for any change in preferences.
        if (key == "adapter_address") {
            val adapterAddress = sharedPreferences.getString("adapter_address", null)
            if (adapterAddress != null) {
                App.instance.initBluetooth(adapterAddress)
            }
        }
        listener.invoke()
    }

    fun unregister() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
