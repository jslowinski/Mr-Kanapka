package com.swapi.swapikotlin.api

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class SwapiClient {

  companion object {

    private const val BASE_URL = "https://swapi.co/api/"

    private val retrofit by lazy {
      Retrofit.Builder()
          .addCallAdapterFactory(
              RxJava2CallAdapterFactory.create())
          .addConverterFactory(
              GsonConverterFactory.create())
          .baseUrl(BASE_URL)
          .build()
    }

    fun create(): SwapiService {
      return retrofit.create(SwapiService::class.java)
    }

      fun createDetail(URL: String): SwapiService{
          val retrofit = Retrofit.Builder()
              .baseUrl(URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build()

          return retrofit.create(SwapiService::class.java)
      }
  }
}
