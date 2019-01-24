package com.mrkanapka.mrkanapkakotlin.api.model

data class Response<T>(
    var count: Int,
    var product: T
)