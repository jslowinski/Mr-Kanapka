package com.mrkanapka.mrkanapkakotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LoginUI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_ui)

        val button : Button = this.findViewById(R.id.login_button)
        val register_click_me = findViewById(R.id.register_text) as TextView


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
    }
}
