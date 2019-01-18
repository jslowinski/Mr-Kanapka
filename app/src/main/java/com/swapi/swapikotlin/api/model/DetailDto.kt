package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

class DetailDto {

    @SerializedName("description")
    var description: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("photo_url")
    var photo_url: String? = null

}

