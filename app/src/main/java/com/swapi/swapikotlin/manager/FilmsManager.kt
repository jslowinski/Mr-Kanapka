package com.swapi.swapikotlin.manager

import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.database.AndroidDatabase
import com.swapi.swapikotlin.database.entity.FilmEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

class FilmsManager {
    //region API

    private val swapiService by lazy {
        SwapiClient.create()
    }

    fun downloadFilms(): Completable =
        swapiService
            .fetchFilms()
            .flatMapCompletable {
                saveFilms(it.results)
            }
            .subscribeOn(Schedulers.io())

    //endregion

    //region Database

    private val database by lazy {
        AndroidDatabase.database
    }

    private fun saveFilms(filmDto: List<FilmDto>) =
        Completable.fromAction {
            val entities = filmDto.map {
                FilmEntity(
                    it.title,
                    it.episodeId,
                    it.director,
                    it.producer,
                    it.openingCrawl,
                    it.url
                )
            }
            database.filmDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())


    fun getFilms(): Maybe<List<FilmEntity>> =
        database
            .filmDao()
            .getFilms()
            .subscribeOn(Schedulers.io())
}