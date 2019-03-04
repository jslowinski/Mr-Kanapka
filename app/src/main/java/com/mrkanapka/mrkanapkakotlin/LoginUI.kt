package com.mrkanapka.mrkanapkakotlin

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_login_ui.*

class LoginUI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_ui)

        val button : Button = this.findViewById(R.id.login_button)
        val register_click_me = findViewById(R.id.register_text) as TextView
        val reset_click_me = findViewById(R.id.forgetPass_text) as TextView

        button.setOnClickListener{
            val main = Intent(this, Main2Activity::class.java)
            //foodDetail.putExtra("Name",film.title)
            startActivity(main)
        }

        register_click_me.setOnClickListener {
            val main = Intent(this, RegisterUI::class.java)
            //foodDetail.putExtra("Name",film.title)
            startActivity(main)
        }

        reset_click_me.setOnClickListener{

            withEditText()

        }
    }

    fun withEditText() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.reset_password_popup, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.email_reset)
        builder.setView(dialogLayout)

        builder.show()
    }
}
