package com.mrkanapka.mrkanapkakotlin.api.model

data class ResponseCart<T>(
    var count: Int,
    var price: Float,
    var cart: T
)