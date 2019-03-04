package com.mrkanapka.mrkanapkakotlin.api

import com.mrkanapka.mrkanapkakotlin.api.model.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*
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

  @GET("destinations")
  fun fetchCities(): Single<ResponseCity<List<CityDto>>>

  @GET
  fun fetchDestinations(@Url url: String): Single<ResponseDestination<List<DestinationDto>>>

  @POST("register")
  fun register(@Body body : RegisterRequest): Call<DefaultResponse>

}