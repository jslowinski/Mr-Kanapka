package com.mrkanapka.mrkanapkakotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class HistoryDetail : AppCompatActivity() {

    var accessToken = ""
    var orderNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        accessToken = intent.getStringExtra("token")
        orderNumber = intent.getStringExtra("orderNumber")
        Log.e("Token", accessToken)
        Log.e("OrderNumber", orderNumber)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Zamówienie: $orderNumber"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }
}
