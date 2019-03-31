package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseDestination<T>(
    var count: Int,
    var destinations: T
)