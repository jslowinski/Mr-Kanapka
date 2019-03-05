package com.mrkanapka.mrkanapkakotlin.manager

import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TokenManager {
    private val database by lazy {
        AndroidDatabase.database
    }

    fun saveToken(token: String) =
        Completable.fromAction {
            val tokenEntity = TokenEntity(token)
            database.tokenDao().removeAndInsert(tokenEntity)
        }.subscribeOn(Schedulers.io())

    fun getToken(): Single<TokenEntity> =
        database
            .tokenDao()
            .getToken()
            .subscribeOn(Schedulers.io())
}