package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseCity<T>(
    var count: Int,
    var cities: T
)