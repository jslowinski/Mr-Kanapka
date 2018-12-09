package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

data class PlanetDto (

    @SerializedName("name")
    var name: String,

    @SerializedName("climate")
    var climate: String,

    @SerializedName("orbital_preoid")
    var orbital_preoid: String
)