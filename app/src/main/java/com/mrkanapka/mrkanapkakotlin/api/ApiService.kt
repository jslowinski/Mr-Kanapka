package com.mrkanapka.mrkanapkakotlin.api

import com.mrkanapka.mrkanapkakotlin.api.model.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

import retrofit2.http.Url

interface ApiService {

  @GET("Kanapka")
  fun fetchSandwiches(): Single<Response<List<ProductsDto>>>

  @GET("Salatka")
  fun fetchSalads(): Single<Response<List<ProductsDto>>>

  @GET("Sok")
  fun fetchJuice(): Single<Response<List<ProductsDto>>>

  @GET
  fun fetchDetail(@Url url: String): Call<ResponseDetail>

}