package com.swapi.swapikotlin.api

import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.api.model.PlanetDto
import com.swapi.swapikotlin.api.model.Response
import io.reactivex.Single
import retrofit2.http.GET

import retrofit2.http.Url

interface SwapiService {

  @GET("films")
  fun fetchFilms(): Single<Response<List<FilmDto>>>

  @GET
  fun fetchPlanets(@Url user_id: String): Single<Response<List<PlanetDto>>>

}