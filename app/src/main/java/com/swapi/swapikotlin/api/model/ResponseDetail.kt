package com.swapi.swapikotlin.api.model

data class ResponseDetail<T>(
    var product: T
)