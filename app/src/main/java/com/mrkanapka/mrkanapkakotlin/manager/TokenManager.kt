package com.mrkanapka.mrkanapkakotlin.manager

import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.CategoryDto
import com.mrkanapka.mrkanapkakotlin.api.model.SellerDto
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase
import com.mrkanapka.mrkanapkakotlin.database.entity.CategoryEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.SellerEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TokenManager {
    private val database by lazy {
        AndroidDatabase.database
    }

    private val apiService by lazy {
        ApiClient.create()
    }

    fun saveToken(token: String, id_destination: Int) =
        Completable.fromAction {
            val tokenEntity = TokenEntity(token, id_destination)
            database.tokenDao().removeAndInsert(tokenEntity)
        }.subscribeOn(Schedulers.io())

    fun getToken(): Single<TokenEntity> =
        database
            .tokenDao()
            .getToken()
            .subscribeOn(Schedulers.io())


    private fun saveSellers(sellerDto: List<SellerDto>, destination: Int) =
        Completable.fromAction {
            val entities = sellerDto.map {
                SellerEntity(
                    it.id_seller,
                    it.sellername,
                    destination
                )
            }
            database.sellerDao().removeAndInsert(entities, destination)
        }.subscribeOn(Schedulers.io())

    fun getSellers(position: Int): Maybe<List<SellerEntity>> =
        database
            .sellerDao()
            .getSellers(position)
            .subscribeOn(Schedulers.io())

    fun downloadSellers(url: String, destination: Int): Completable =
        apiService
            .fetchSellers(url)
            .flatMapCompletable {
                saveSellers(it.seller, destination)
            }
            .subscribeOn(Schedulers.io())

    private fun saveCategory(categoryDto: List<CategoryDto>, seller: Int) =
        Completable.fromAction {
            val entities = categoryDto.map {
                CategoryEntity(
                    it.id_category,
                    it.name,
                    seller
                )
            }
            database.categoryDao().removeAndInsert(entities, seller)
        }.subscribeOn(Schedulers.io())

    fun getCategory(position: Int): Maybe<List<CategoryEntity>> =
        database
            .categoryDao()
            .getCategory(position)
            .subscribeOn(Schedulers.io())

    fun downloadCategory(url: String, seller: Int): Completable =
        apiService
            .fetchCategory(url)
            .flatMapCompletable {
                saveCategory(it.category, seller)
            }
            .subscribeOn(Schedulers.io())
}