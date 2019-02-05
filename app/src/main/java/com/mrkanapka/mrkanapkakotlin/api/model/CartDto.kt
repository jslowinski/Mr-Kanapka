package com.mrkanapka.mrkanapkakotlin.api.model

import com.google.gson.annotations.SerializedName

data class CartDto(

    @SerializedName("url")
    var url: Int,

    @SerializedName("title")
    var title: String,

    @SerializedName("price")
    var price: String,

    @SerializedName("quantity")
    var quantity: Int,

    @SerializedName("id_product")
    var id_product: Int,

    @SerializedName("photo_url")
    var photo_url: String
)