package com.mrkanapka.mrkanapkakotlin.api.model.Request

data class RequestDeleteCart(
    var access_token: String,
    var id_product: Int
)