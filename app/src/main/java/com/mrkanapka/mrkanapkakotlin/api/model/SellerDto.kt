package com.mrkanapka.mrkanapkakotlin.api.model

import com.google.gson.annotations.SerializedName

data class SellerDto (
    @SerializedName("id_seller")
    var id_seller: Int,

    @SerializedName("sellername")
    var sellername: String? = null
)
