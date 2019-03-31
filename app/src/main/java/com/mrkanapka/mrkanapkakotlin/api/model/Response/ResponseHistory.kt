package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseHistory<T> (
    var next: Int,
    var orders: T
)
