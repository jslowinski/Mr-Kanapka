package com.mrkanapka.mrkanapkakotlin.api.model

data class ResponseCartDetail(
    var amount: Int,
    var id_product: Int,
    var name: String,
    var photo_url: String,
    var price: String
)