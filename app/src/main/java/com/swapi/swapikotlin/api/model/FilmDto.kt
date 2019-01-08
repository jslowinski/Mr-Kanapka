package com.swapi.swapikotlin.api.model

import com.google.gson.annotations.SerializedName

data class FilmDto(

    @SerializedName("title")
    var title: String,

    @SerializedName("director")
    var director: String,

    @SerializedName("producer")
    var producer: String,

    @SerializedName("opening_crawl")
    var openingCrawl: String,

    @SerializedName("url")
    var url: String
)