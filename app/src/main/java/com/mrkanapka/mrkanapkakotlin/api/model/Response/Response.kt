package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class Response<T>(
    var count: Int,
    var product: T
)