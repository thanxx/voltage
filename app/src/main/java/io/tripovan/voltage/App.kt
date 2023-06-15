package io.tripovan.voltage

import android.app.Application
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(instance.applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }
}