package com.swapi.swapikotlin.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "films")
data class FilmEntity(

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "episode_id")
    var episodeId: Int,

    @ColumnInfo(name = "director")
    var director: String,

    @ColumnInfo(name = "producer")
    var producer: String,

    @ColumnInfo(name = "opening_crawl")
    var openingCrawl: String,

    @ColumnInfo(name = "url")
    var url: String
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}