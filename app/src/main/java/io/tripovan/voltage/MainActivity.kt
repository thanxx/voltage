package io.tripovan.voltage

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.tripovan.voltage.databinding.ActivityMainBinding
import io.tripovan.voltage.utils.Constants


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var layout: View
    private lateinit var sharedPreferencesChangeListener: SharedPreferencesChangeListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_Voltage)

        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView




        val sharedPref = App.instance.getSharedPrefs()
        sharedPreferencesChangeListener = SharedPreferencesChangeListener(this, sharedPref!!) {
            val adapterAddress = sharedPref.getString("adapter_address", null)
            if (adapterAddress != null) {
                App.instance.initBluetooth(adapterAddress)
            }
        }

        layout = binding.navView
        binding.loadingPanel.visibility = View.GONE

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        if (!sharedPref.getString("adapter_address", null).isNullOrEmpty()) {
            navView.selectedItemId = R.id.navigation_dashboard
        }

    }





    override fun onDestroy() {
        super.onDestroy()
        sharedPreferencesChangeListener.unregister()
    }
}