package com.mrkanapka.mrkanapkakotlin.api.model

data class ResponseCity<T>(
    var count: Int,
    var cities: T
)