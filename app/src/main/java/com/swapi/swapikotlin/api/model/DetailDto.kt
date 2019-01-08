package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

class DetailDto {

    @SerializedName("title")
    var title: String? = null

    @SerializedName("opening_crawl")
    var openingCrawl: String? = null
}

