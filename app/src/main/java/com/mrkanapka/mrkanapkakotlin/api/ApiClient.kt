package com.mrkanapka.mrkanapkakotlin.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

  companion object {

    private const val BASE_URL = "http://zespol9-server.herokuapp.com/api/"

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

      fun createDestination(url: String): ApiService {
          val retrofit = Retrofit.Builder()
              .addCallAdapterFactory(
                  RxJava2CallAdapterFactory.create())
              .baseUrl(url)
              .addConverterFactory(GsonConverterFactory.create())
              .build()
          return retrofit.create(ApiService::class.java)
      }


      fun createDetail(URL: String): ApiService{
          val retrofit = Retrofit.Builder()
              .baseUrl(URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build()

          return retrofit.create(ApiService::class.java)
      }

      private val okHttpClient = OkHttpClient.Builder()
          .addInterceptor{chain ->
              val original = chain.request()

              val requestBuilder  = original.newBuilder()
                  .addHeader("Content-Type", "application/json")
                  .method(original.method(),original.body())

              val request = requestBuilder.build()
              chain.proceed(request)
          }.build()

      val instance: ApiService by lazy{
          val retrofit = Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .client(okHttpClient)
              .build()

          retrofit.create(ApiService::class.java)
      }
  }
}
