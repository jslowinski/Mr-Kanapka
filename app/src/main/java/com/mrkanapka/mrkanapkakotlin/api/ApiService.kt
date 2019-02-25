package com.mrkanapka.mrkanapkakotlin.api

import com.mrkanapka.mrkanapkakotlin.api.model.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

import retrofit2.http.Url

interface ApiService {

  @GET("products/Kanapka")
  fun fetchSandwiches(): Single<Response<List<ProductsDto>>>

  @GET("products/Salatka")
  fun fetchSalads(): Single<Response<List<ProductsDto>>>

  @GET("products/Sok")
  fun fetchJuice(): Single<Response<List<ProductsDto>>>

  @GET
  fun fetchDetail(@Url url: String): Call<ResponseDetail>

  @GET("products")
  fun fetchCategory(): Single<ResponseCategory<List<CategoryDto>>>

}