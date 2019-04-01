package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseOrder (
    var id_status: String,
    var full_price: String,
    var order_number: String,
    var date: String,
    var message: String,
    var products: List<ResponseCartDetail>
    )