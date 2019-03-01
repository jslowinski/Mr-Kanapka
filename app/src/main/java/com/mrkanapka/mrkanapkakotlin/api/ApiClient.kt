package com.mrkanapka.mrkanapkakotlin.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

  companion object {

    private const val BASE_URL = "http://zespol9-server.herokuapp.com/api/products/"

    private const val BASE_URL_REGISTER = "http://zespol9-server.herokuapp.com/api/"

    private val retrofit by lazy {
      Retrofit.Builder()
          .addCallAdapterFactory(
              RxJava2CallAdapterFactory.create())
          .addConverterFactory(
              GsonConverterFactory.create())
          .baseUrl(BASE_URL)
          .build()
    }

    fun create(): ApiService {
      return retrofit.create(ApiService::class.java)
    }

      fun createDetail(URL: String): ApiService{
          val retrofit = Retrofit.Builder()
              .baseUrl(URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build()

          return retrofit.create(ApiService::class.java)
      }


      val instance: ApiService by lazy{
          val retrofit = Retrofit.Builder()
              .baseUrl(BASE_URL_REGISTER)
              .addConverterFactory(GsonConverterFactory.create())
              .build()

          retrofit.create(ApiService::class.java)
      }
  }
}
