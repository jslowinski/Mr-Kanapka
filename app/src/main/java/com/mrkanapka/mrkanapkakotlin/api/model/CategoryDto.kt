package com.mrkanapka.mrkanapkakotlin.api.model

import com.google.gson.annotations.SerializedName

data class CategoryDto(

    @SerializedName("id_category")
    var id_category: Int,

    @SerializedName("name")
    var name: String

)