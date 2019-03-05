package com.mrkanapka.mrkanapkakotlin.api

import com.mrkanapka.mrkanapkakotlin.api.model.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Url

interface ApiService {

  @GET
  fun fetchProducts(@Url url: String): Single<Response<List<ProductsDto>>>

  @GET("products")
  fun fetchCategory(): Single<ResponseCategory<List<CategoryDto>>>

  @GET
  fun fetchDetail(@Url url: String): Call<ResponseDetail>

  @GET("destinations")
  fun fetchCities(): Single<ResponseCity<List<CityDto>>>

  @GET
  fun fetchDestinations(@Url url: String): Single<ResponseDestination<List<DestinationDto>>>

  @POST("register")
  fun register(@Body body : RequestRegister): Call<ResponseDefault>

  @PUT("login")
  fun login(@Body body : RequestLogin): Call<ResponseDefault>

}