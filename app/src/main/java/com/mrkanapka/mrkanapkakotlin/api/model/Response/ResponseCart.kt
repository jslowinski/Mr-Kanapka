package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseCart<T>(
    var count: Int,
    var full_price: Float,
    var cart: T,
    var seller_name: String
)