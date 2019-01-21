package com.swapi.swapikotlin.manager

import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.model.ProductsDto
import com.swapi.swapikotlin.database.AndroidDatabase
import com.swapi.swapikotlin.database.entity.JuiceEntity
import com.swapi.swapikotlin.database.entity.ProductEntity
import com.swapi.swapikotlin.database.entity.SaladEntity
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

    fun downloadSalads(): Completable =
        swapiService
            .fetchSalads()
            .flatMapCompletable {
                saveSalads(it.product)
            }
            .subscribeOn(Schedulers.io())

    fun downloadJuices(): Completable =
        swapiService
            .fetchJuice()
            .flatMapCompletable {
                saveJuices(it.product)
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

    private fun saveSalads(productsDto: List<ProductsDto>) =
        Completable.fromAction {
            val entities = productsDto.map {
                SaladEntity(
                    it.name,
                    it.id_product,
                    it.id_seller,
                    it.photo_url,
                    it.price,
                    it.description
                )
            }
            database.saladDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())

    private fun saveJuices(productsDto: List<ProductsDto>) =
        Completable.fromAction {
            val entities = productsDto.map {
                JuiceEntity(
                    it.name,
                    it.id_product,
                    it.id_seller,
                    it.photo_url,
                    it.price,
                    it.description
                )
            }
            database.juiceDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())


    fun getFilms(): Maybe<List<ProductEntity>> =
        database
            .filmDao()
            .getFilms()
            .subscribeOn(Schedulers.io())

    fun getSalads(): Maybe<List<SaladEntity>> =
        database
            .saladDao()
            .getSalads()
            .subscribeOn(Schedulers.io())

    fun getJuices(): Maybe<List<JuiceEntity>> =
        database
            .juiceDao()
            .getJuices()
            .subscribeOn(Schedulers.io())
}