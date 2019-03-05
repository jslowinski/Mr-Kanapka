package com.mrkanapka.mrkanapkakotlin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.*
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.RequestLogin
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_ui.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginUI : AppCompatActivity() {

    private var emailInput: String = ""
    private var passwordInput: String = ""

    private val apiService by lazy {
        ApiClient.create()
    }

    private val database by lazy {
        AndroidDatabase.database
    }

    private val tokenManager by lazy {
        TokenManager()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    private fun handleTokenCacheSuccess(token: TokenEntity) {
        val main = Intent(this, Main2Activity::class.java)
        startActivity(main)
        finish()
    }

    private fun handleTokenCacheError(throwable: Throwable) {

        // Log an error.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_ui)

        val button : Button = this.findViewById(R.id.login_button)
        val register_click_me = findViewById<TextView>(R.id.register_text)
        val reset_click_me = findViewById<TextView>(R.id.forgetPass_text)


        tokenManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)

        button.setOnClickListener{

            emailInput = email_text.text.toString().trim()
            passwordInput = password_text.text.toString().trim()
            val main = Intent(this, Main2Activity::class.java)

            if(this!!.hasNetwork(applicationContext)!!)
            {
                apiService.login(RequestLogin(emailInput,passwordInput))
                    .enqueue(object : Callback<ResponseDefault>{
                        override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                            print("blad")
                        }

                        @SuppressLint("CheckResult")
                        override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                            if(response.code() == 200)
                            {
                                Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_LONG).show()
                                Completable.fromAction {
                                    database
                                        .tokenDao()
                                        .removeAndInsert(TokenEntity(response.body()!!.message))
                                }.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        // data updated
                                    }
                                startActivity(main)
                            }
                            else if (response.code() == 202)
                            {
                                Toast.makeText(applicationContext,"Niepoprawne dane logowania", Toast.LENGTH_LONG).show()
                            }
                            else
                            {
                                Toast.makeText(applicationContext,"Bład przy logowaniu", Toast.LENGTH_LONG).show()
                            }
                        }

                    })
            }
            else
            {
                Toast.makeText(applicationContext,"Brak internetu", Toast.LENGTH_LONG).show()
            }


        }

        register_click_me.setOnClickListener {

            if(this.hasNetwork(applicationContext)!!) {
                val main = Intent(this, RegisterUI::class.java)
                //foodDetail.putExtra("Name",film.title)
                startActivity(main)
            }
            else {
                Toast.makeText(applicationContext,"Sprawdź swoje połączenie z internetem", Toast.LENGTH_LONG).show()
            }

        }

        reset_click_me.setOnClickListener{
            resetPasswordPopup()
        }
    }

    fun resetPasswordPopup() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.reset_password_popup, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.email_reset)
        builder.setView(dialogLayout)

        builder.show()
    }

    fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }
}
