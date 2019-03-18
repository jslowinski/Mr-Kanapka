package com.mrkanapka.mrkanapkakotlin.manager

import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.ProductsDto
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase
import com.mrkanapka.mrkanapkakotlin.database.entity.ProductEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

class ProductsManager {
    //region API

    private val apiService by lazy {
        ApiClient.create()
    }

    fun downloadProducts(url: String, category: Int, id_seller: Int): Completable =
        apiService
            .fetchProducts(url)
            .flatMapCompletable {
                saveProducts(it.product, category, id_seller)
            }
            .subscribeOn(Schedulers.io())

    //endregion

    //region Database

    private val database by lazy {
        AndroidDatabase.database
    }

    private fun saveProducts(
        productsDto: List<ProductsDto>,
        category: Int,
        id_seller: Int
    ) =
        Completable.fromAction {
            val entities = productsDto.map {
                ProductEntity(
                    it.name,
                    it.id_product,
                    it.id_seller,
                    it.photo_url,
                    it.price,
                    it.description,
                    category,
                    id_seller
                )
            }
            database.productDao().removeAndInsert(entities, category, id_seller)
        }.subscribeOn(Schedulers.io())


    fun getProducts(position: Int, id_seller: Int): Maybe<List<ProductEntity>> =
        database
            .productDao()
            .getProducts(position, id_seller)
            .subscribeOn(Schedulers.io())
}