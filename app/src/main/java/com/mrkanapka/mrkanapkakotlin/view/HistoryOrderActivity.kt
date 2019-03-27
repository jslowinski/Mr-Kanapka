package com.mrkanapka.mrkanapkakotlin.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mrkanapka.mrkanapkakotlin.R

class HistoryOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_order)

        val actionBar = supportActionBar
        actionBar!!.title = "Historia zamówień"
        actionBar.setDisplayHomeAsUpEnabled(true)



    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
