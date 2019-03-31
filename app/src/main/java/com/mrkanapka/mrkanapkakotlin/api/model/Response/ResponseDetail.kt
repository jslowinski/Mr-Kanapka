package com.mrkanapka.mrkanapkakotlin.api.model.Response

import com.google.gson.annotations.SerializedName
import com.mrkanapka.mrkanapkakotlin.api.model.DetailDto

data class ResponseDetail(
    @SerializedName("c_count")
    var c_count: String? = null,

    @SerializedName("component")
    var component: List<DetailDto>,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("photo_url")
    var photo_url: String? = null,

    @SerializedName("price")
    var price: String? = null
)