package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

data class CartDto(

    @SerializedName("url")
    var url: String,

    @SerializedName("title")
    var title: String,

    @SerializedName("quantity")
    var quantity: Int
)