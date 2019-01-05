package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

data class CartDto(

    @SerializedName("title")
    var title: String,

    @SerializedName("quantity")
    var quantity: Int
)