package com.swapi.swapikotlin.manager

import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.model.ProductsDto
import com.swapi.swapikotlin.database.AndroidDatabase
import com.swapi.swapikotlin.database.entity.ProductEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

class ProductsManager {
    //region API

    private val swapiService by lazy {
        SwapiClient.create()
    }

    fun downloadFilms(): Completable =
        swapiService
            .fetchFilms()
            .flatMapCompletable {
                saveFilms(it.product)
            }
            .subscribeOn(Schedulers.io())

    //endregion

    //region Database

    private val database by lazy {
        AndroidDatabase.database
    }

    private fun saveFilms(productsDto: List<ProductsDto>) =
        Completable.fromAction {
            val entities = productsDto.map {
                ProductEntity(
                    it.name,
                    it.id_product,
                    it.id_seller,
                    it.photo_url,
                    it.price,
                    it.description
                )
            }
            database.filmDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())


    fun getFilms(): Maybe<List<ProductEntity>> =
        database
            .filmDao()
            .getFilms()
            .subscribeOn(Schedulers.io())
}