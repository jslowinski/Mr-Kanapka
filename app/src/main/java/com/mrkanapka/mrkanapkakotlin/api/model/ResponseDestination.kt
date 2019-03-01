package com.mrkanapka.mrkanapkakotlin.api.model

data class ResponseDestination<T>(
    var count: Int,
    var destinations: T
)