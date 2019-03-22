package com.mrkanapka.mrkanapkakotlin.api

import com.mrkanapka.mrkanapkakotlin.api.model.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Url

interface ApiService {

  @GET
  fun fetchProducts(@Url url: String): Single<Response<List<ProductsDto>>>

  @GET
  fun fetchSellers(@Url url: String): Single<ResponseSeller<List<SellerDto>>>

  @GET
  fun fetchCategory(@Url url: String): Single<ResponseCategory<List<CategoryDto>>>

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

  @PUT("profile")
  fun fetchProfile(@Body body: RequestToken): Call<ResponseProfile>

  @PUT("profile/edit")
  fun editProfile(@Body body: RequestProfileEdit): Call<ResponseDefault>

  @PUT("token_check")
  fun checkToken(@Body body: RequestToken): Call<ResponseDefault>

  @POST("forgot")
  fun forgotPassword(@Body body: RequestResetPassword): Call<ResponseDefault>

  @PUT("add_cart")
  fun addCart(@Body body: RequestAddCart): Call<ResponseDefault>

  @PUT("remove_one")
  fun removeOne(@Body body: RequestRemoveOne): Call<ResponseDefault>

  @PUT ("remove_all")
  fun deleteProductCart(@Body body: RequestDeleteCart): Call<ResponseDefault>

  @POST("show_cart")
  fun fetchCart(@Body body: RequestToken): Call<ResponseCart<List<ResponseCartDetail>>>

}