package com.mrkanapka.mrkanapkakotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ProfilUI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_ui)

        val actionBar = supportActionBar
        actionBar!!.title = "Profil"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
