package com.mrkanapka.mrkanapkakotlin.api.model

data class ResponseCategory<T>(
    var category: T,
    var count: Int,
    var results: T
)