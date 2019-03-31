package com.mrkanapka.mrkanapkakotlin.api.model.Request

data class RequestAddCart(
    var access_token: String,
    var id_product: Int,
    var amount: Int
)