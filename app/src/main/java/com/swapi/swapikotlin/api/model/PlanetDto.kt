package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

data class PlanetDto (

    @SerializedName("name")
    var name: String,

    @SerializedName("rotation_period")
    var rotation_period: String,

    @SerializedName("orbital_period")
    var orbital_period: String,

    @SerializedName("url")
    var url: String
)