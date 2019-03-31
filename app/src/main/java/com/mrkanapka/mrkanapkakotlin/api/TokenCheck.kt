package com.mrkanapka.mrkanapkakotlin.api

import android.util.Log
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestToken
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenCheck {

    companion object {
        private val apiService by lazy {
            ApiClient.create()
        }

        private val tokenManager by lazy {
            TokenManager()
        }

        private val disposables: CompositeDisposable = CompositeDisposable()

        private var access_token : String = ""

        var status : Boolean = false

        private fun handleTokenCacheSuccess(token: TokenEntity) {
            Log.e("Token z handle: ", token.token)
            //Zadziala jak tutaj wykonamy checktoken z argumentem token.token nie zawsze chce podpisać pod zmienną access_token zadziala jak wyjdzie sie z aktywnosci i wejdzie jeszcze raz
            access_token = token.token
            Log.e("Token z handle access: ", access_token)
            checkToken()
        }

        private fun handleTokenCacheError(throwable: Throwable) {
            Log.e("Error", " wywołałem się")
        }

        private fun checkToken(){
            Log.e("Access token z check: ", access_token)

            apiService.checkToken(RequestToken(access_token))
                .enqueue(object : Callback<ResponseDefault>{
                    override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                        Log.e("Status: ", "Fail connection")
                    }

                    override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                        if (response.code() == 200) //Good token
                        {
                            Log.e("Status: ", "Good token")
                            status = true
                        }
                        if (response.code() == 204) //Bad token
                        {
                            Log.e("Status: ", "Bad")
                            status = false

                        }
                    }
                })
        }

        fun sessionStatus() {
            tokenManager
                .getToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleTokenCacheSuccess,
                    this::handleTokenCacheError
                )
                .addTo(disposables)

            //Log.e("Status: ", "OK")
        }
    }
}