package com.swapi.swapikotlin.api.model

data class Response<T>(
    var count: Int,
    var next: String,
    var previous: String,
    var results: T
)