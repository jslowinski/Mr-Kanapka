package com.mrkanapka.mrkanapkakotlin.api.model

import com.google.gson.annotations.SerializedName

class CityDto {
    @SerializedName("city")
    var city: String? = null

    @SerializedName("id_city")
    var id_city: Int? = null

}