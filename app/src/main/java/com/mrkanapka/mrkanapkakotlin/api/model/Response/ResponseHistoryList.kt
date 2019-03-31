package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseHistoryList(
    var date: String,
    var full_price: String,
    var order_number: String,
    var status: String
)