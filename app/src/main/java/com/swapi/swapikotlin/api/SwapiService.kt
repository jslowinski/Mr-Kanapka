package com.swapi.swapikotlin.api

import com.swapi.swapikotlin.api.model.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

import retrofit2.http.Url

interface SwapiService {

  @GET("Kanapka")
  fun fetchFilms(): Single<Response<List<ProductsDto>>>

  @GET
  fun fetchPlanets(@Url user_id: String): Single<Response<List<PlanetDto>>>

  @GET
  fun fetchDetail(@Url url: String): Call<ResponseDetail>

}