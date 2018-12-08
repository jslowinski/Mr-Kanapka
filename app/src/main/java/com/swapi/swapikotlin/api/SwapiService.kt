package com.swapi.swapikotlin.api

import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.api.model.PlanetDto
import com.swapi.swapikotlin.api.model.Response
import io.reactivex.Single
import retrofit2.http.GET

interface SwapiService {

  @GET("films")
  fun fetchFilms(): Single<Response<List<FilmDto>>>

  @GET("planets")
  fun fetchPlanets(): Single<Response<List<PlanetDto>>>

}