package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

data class ProductsDto(

    @SerializedName("name")
    var name: String,

    @SerializedName("id_product")
    var id_product: Int,

    @SerializedName("id_seller")
    var id_seller: Int,

    @SerializedName("photo_url")
    var photo_url: String,

    @SerializedName("price")
    var price: String

)