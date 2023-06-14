package io.tripovan.voltage

import android.app.Application
import android.content.Context

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
}