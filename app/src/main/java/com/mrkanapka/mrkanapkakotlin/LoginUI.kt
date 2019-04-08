package com.mrkanapka.mrkanapkakotlin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.Patterns
import android.widget.*
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestLogin
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestResetPassword
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestToken
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDefault
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
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseProfile


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

    private fun startMenu() {
        val main = Intent(this, Main2Activity::class.java)
        startActivity(main)
        finish()
    }

    private fun handleTokenCacheSuccess(token: TokenEntity) {

        println(token.token)

        apiService.checkToken(RequestToken(token.token))
            .enqueue(object : Callback<ResponseDefault>{
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    Log.e("Status: ", "Fail connection")
                    Log.e("Token ", token.token)
                    Toast.makeText(applicationContext, "Brak internetu, tryb offline", Toast.LENGTH_LONG).show()
                    dialog.cancel()
                    startMenu()
                }

                @SuppressLint("CheckResult")
                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if (response.code() == 200) //Good token
                    {
                        startMenu()
                    }
                    if (response.code() == 400) //Bad token
                    {
                        //Zmień token na offline żeby wejść bez neta w tryb wyświetlania, ale nie wyświetlaj za każdym razem komunikatu, że zalogowano się z innego urządzenia
                        if (token.token.equals("offline"))
                        {
                            dialog.cancel()
                        } else {
                            dialog.cancel()
                            Toast.makeText(applicationContext, "Zalogowano się z innego urządzenia\nZaloguj się ponownie", Toast.LENGTH_LONG).show()
                            Completable.fromAction {
                                database
                                    .tokenDao()
                                    .removeAndInsert(TokenEntity("offline", -1))
                            }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    // data updated
                                }
                        }
                        Log.e("Token: ", token.token)

                    }
                }
            })

    }

    private fun handleTokenCacheError(throwable: Throwable) {

        dialog.cancel()
        // Log an error.

    }

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_ui)

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token



                Log.e("...", token)
                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })

        val button : Button = this.findViewById(R.id.login_button)
        val register_click_me = findViewById<TextView>(R.id.register_text)
        val reset_click_me = findViewById<TextView>(R.id.forgetPass_text)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.textDialog)
        message.text = "Sprawdzanie danych logowania..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
        tokenManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)

        button.setOnClickListener{

            dialog.show()
            emailInput = email_text.text.toString().trim()
            passwordInput = password_text.text.toString().trim()
            val main = Intent(this, Main2Activity::class.java)

            if(this!!.hasNetwork(applicationContext)!!)
            {
                apiService.login(
                    RequestLogin(
                        emailInput,
                        passwordInput
                    )
                )
                    .enqueue(object : Callback<ResponseDefault>{
                        override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                            dialog.cancel()
                            Toast.makeText(applicationContext, "Wystąpił błąd. Spróbuj ponownie", Toast.LENGTH_LONG).show()
                        }

                        @SuppressLint("CheckResult")
                        override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                            when {
                                response.code() == 200 -> {
                                    //Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_LONG).show()
                                    val token = response.body()!!.message
                                    apiService.fetchProfile(RequestToken(token))
                                        .enqueue(object : Callback<ResponseProfile>{
                                            override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
                                                Toast.makeText(applicationContext, "Wystąpił błąd. Spróbuj ponownie", Toast.LENGTH_LONG).show()
                                            }

                                            override fun onResponse(call: Call<ResponseProfile>, response: Response<ResponseProfile>) {

                                                val id_destination_profile = response.body()!!.id_destination
                                                Completable.fromAction {
                                                    database
                                                        .tokenDao()
                                                        .removeAndInsert(TokenEntity(token, id_destination_profile))
                                                }.subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe {
                                                        // data updated
                                                    }
                                                startActivityForResult(main,1)
                                                finish()
                                            }
                                        })


                                }
                                response.code() == 202 -> {
                                    dialog.cancel()
                                    Toast.makeText(applicationContext,"Niepoprawne dane logowania", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    dialog.cancel()
                                    Toast.makeText(applicationContext,"Bład przy logowaniu", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                    })
            }
            else
            {
                dialog.cancel()
                Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
            }


        }

        register_click_me.setOnClickListener {

            if(this.hasNetwork(applicationContext)!!) {
                val main = Intent(this, RegisterUI::class.java)
                //foodDetail.putExtra("Name",film.title)
                startActivity(main)
                finish()
            }
            else {
                Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
            }

        }

        reset_click_me.setOnClickListener{
            resetPasswordPopup()
        }
    }
    private lateinit var emailDialog: AlertDialog
    fun resetPasswordPopup() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.reset_password_popup, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.email_reset)
        val buttonSend = dialogLayout.findViewById<Button>(R.id.restet_button)

        buttonSend.setOnClickListener{
            val email = editText.text.toString().trim()
            if(validateEmail(email))
            {
                apiService.forgotPassword(RequestResetPassword(email))
                    .enqueue(object : Callback<ResponseDefault>{
                        override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                            emailDialog.cancel()
                            Toast.makeText(applicationContext, "Wystąpił błąd. Spróbuj ponownie", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                            if (response.code() == 200){
                                emailDialog.cancel()
//                                val toast = Toast.makeText(applicationContext, "Message", Toast.LENGTH_SHORT)
//                                toast.setGravity(Gravity.CENTER, 0, 0)
//                                toast.show()
                                Toast.makeText(applicationContext, "Na podany email wysłano link z resetem hasła", Toast.LENGTH_LONG).show()
                            }
                            else {
                                emailDialog.cancel()
                                Toast.makeText(applicationContext, "Wystąpił błąd. Spróbuj ponownie", Toast.LENGTH_LONG).show()
                            }

                        }

                    })
            }
            else
            {
                editText.setError("Niepoprawny adres email")
            }

        }
        builder.setView(dialogLayout)

        emailDialog = builder.create()
        emailDialog.show()
    }

    fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }

    private fun validateEmail(email : String): Boolean {
        return !(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        }
}

