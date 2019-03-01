package com.mrkanapka.mrkanapkakotlin


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_register_ui.*
import java.util.regex.Pattern

class RegisterUI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_ui)
        setCitySpinner()
        setOfficeSpinner()
        val button: Button = this.findViewById(R.id.register_button)

        button.setOnClickListener {


            if (validateEmail() && validatePassword())
            {
                println("Test")
            }
        }

    }

    private fun setCitySpinner() {
        val citySpinner: Spinner = findViewById(R.id.city_register)
        val myStrings = arrayOf("Select Item", "One", "Two", "Three")
        var adapter = ArrayAdapter(this, R.layout.spinner_item, myStrings)
        citySpinner.adapter = adapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = adapter.getItem(position)
            }
        }
    }

    private fun setOfficeSpinner() {
        val citySpinner: Spinner = findViewById(R.id.office_register)
        val myStrings = arrayOf("Select Office", "1", "2", "3")
        var adapter = ArrayAdapter(this, R.layout.spinner_item, myStrings)
        citySpinner.adapter = adapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = adapter.getItem(position)
            }
        }
    }

    private fun validateEmail(): Boolean {
        val emailInput = email_register.text.toString().trim()

        if (emailInput.isEmpty()) {
            email_register.setError("Pole nie może być puste")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email_register.setError("Niepoprawny adres email")
            return false
        } else {
            email_register.setError(null)
            return true
        }
    }

    private val PASSWORD_PATTERN = Pattern.compile(
        "^" +
        "(?=.*[0-9])" +         //at least 1 digit
        "(?=.*[a-z])" +         //at least 1 lower case letter
        "(?=.*[A-Z])" +         //at least 1 upper case letter
        //"(?=.*[a-zA-Z])" +      //any letter
        "(?=.*[@#$%^&+=])" +    //at least 1 special character
        "(?=\\S+$)" +           //no white spaces
        ".{6,}" +               //at least 4 characters
        "$")

    private fun validatePassword(): Boolean {
        val passwordInput = password_register.text.toString().trim()

        if (passwordInput.isEmpty()) {
            password_register.setError("Pole nie może być puste")
            return false
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            password_register.setError("Hasło za słabe")
            return false
        } else {
            password_register.setError(null)
            return true
        }
    }

}
