package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseCategory<T>(
    var category: T,
    var count: Int

)