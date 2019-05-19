package com.mrkanapka.mrkanapkakotlin.api.model

import com.google.gson.annotations.SerializedName

data class DestinationDto(

    @SerializedName("city")
    var city: String,

    @SerializedName("house_number")
    var house_number: String,

    @SerializedName("id_destination")
    var id_destination: Int,

    @SerializedName("name")
    var name: String,

    @SerializedName("street")
    var street: String
)