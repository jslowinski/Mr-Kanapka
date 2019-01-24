package com.mrkanapka.mrkanapkakotlin.application

import android.app.Application
import com.facebook.stetho.Stetho
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this)

        // Initialize database.
        AndroidDatabase.initialize(this)
    }
}