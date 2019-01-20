package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

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