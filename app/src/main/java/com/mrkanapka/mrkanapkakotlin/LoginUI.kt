package com.mrkanapka.mrkanapkakotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.*
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.RequestLogin
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseDefault
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_ui)

        val button : Button = this.findViewById(R.id.login_button)
        val register_click_me = findViewById(R.id.register_text) as TextView
        val reset_click_me = findViewById(R.id.forgetPass_text) as TextView

        button.setOnClickListener{

            emailInput = email_text.text.toString().trim()
            passwordInput = password_text.text.toString().trim()
            val main = Intent(this, Main2Activity::class.java)

            apiService.login(RequestLogin(emailInput,passwordInput))
                .enqueue(object : Callback<ResponseDefault>{
                    override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                        if (response.body()?.message.equals("Niepoprawne dane logowania"))
                        {
                            Toast.makeText(applicationContext,"Niepoprawne dane logowania", Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                            startActivity(main)
                        }
                    }

                })

        }

        register_click_me.setOnClickListener {
            val main = Intent(this, RegisterUI::class.java)
            //foodDetail.putExtra("Name",film.title)
            startActivity(main)
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
}
