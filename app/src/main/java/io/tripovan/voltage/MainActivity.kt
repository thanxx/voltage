package io.tripovan.voltage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.tripovan.voltage.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var layout: View
    private lateinit var sharedPreferencesChangeListener: SharedPreferencesChangeListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Check BT permissions
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 2)
                return
            }
            // BT OK
        } else {
            Toast.makeText(this@MainActivity, "Bluetooth error, please check permissions", Toast.LENGTH_SHORT).show()
        }

        val sharedPref = getSharedPreferences("voltage_settings", Context.MODE_PRIVATE)
        sharedPreferencesChangeListener = SharedPreferencesChangeListener(this, sharedPref) {
            val adapterAddress = sharedPref?.getString("adapter_address", null)
            if (adapterAddress != null) {
                App.instance.initBluetooth(adapterAddress)
            }
            // Code to execute when preferences change
            // For example, update your UI or perform other operations
        }

        layout = binding.navView
        binding.loadingPanel.visibility = View.GONE
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_settings, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferencesChangeListener.unregister()
    }
}