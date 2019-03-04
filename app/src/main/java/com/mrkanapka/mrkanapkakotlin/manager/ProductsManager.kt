package com.mrkanapka.mrkanapkakotlin.manager

import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.ProductsDto
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase
import com.mrkanapka.mrkanapkakotlin.database.entity.JuiceEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.ProductEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.SaladEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

class ProductsManager {
    //region API

    private val apiService by lazy {
        ApiClient.create()
    }

    fun downloadSandwiches(url: String): Completable =
        apiService
            .fetchSandwiches(url)
            .flatMapCompletable {
                saveSandwiches(it.product)
            }
            .subscribeOn(Schedulers.io())

    fun downloadSalads(url: String): Completable =
        apiService
            .fetchSalads(url)
            .flatMapCompletable {
                saveSalads(it.product)
            }
            .subscribeOn(Schedulers.io())

    fun downloadJuices(url: String): Completable =
        apiService
            .fetchJuice(url)
            .flatMapCompletable {
                saveJuices(it.product)
            }
            .subscribeOn(Schedulers.io())

    //endregion

    //region Database

    private val database by lazy {
        AndroidDatabase.database
    }

    private fun saveSandwiches(productsDto: List<ProductsDto>) =
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
            database.sandwichDao().removeAndInsert(entities)
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


    fun getSandwiches(): Maybe<List<ProductEntity>> =
        database
            .sandwichDao()
            .getSandwichs()
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