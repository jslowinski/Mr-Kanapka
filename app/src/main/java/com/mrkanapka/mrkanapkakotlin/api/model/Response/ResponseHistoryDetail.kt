package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseHistoryDetail<T>(
    val date: String,
    val destination: String,
    val flag: Int,
    val full_price: Double,
    val name: String,
    val seller: String,
    val comment: String,
    val products: T
)