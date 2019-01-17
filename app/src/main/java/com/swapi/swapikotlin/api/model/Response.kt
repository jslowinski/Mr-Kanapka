package com.swapi.swapikotlin.api.model

data class Response<T>(
    var count: Int,
    var results: T
)