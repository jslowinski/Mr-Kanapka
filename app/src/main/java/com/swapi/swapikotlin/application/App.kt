package com.swapi.swapikotlin.application

import android.app.Application
import com.facebook.stetho.Stetho
import com.swapi.swapikotlin.database.AndroidDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this)

        // Initialize database.
        AndroidDatabase.initialize(this)
    }
}