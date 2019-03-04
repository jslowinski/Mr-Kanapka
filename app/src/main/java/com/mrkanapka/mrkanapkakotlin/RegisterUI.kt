package com.mrkanapka.mrkanapkakotlin


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.CityDto
import com.mrkanapka.mrkanapkakotlin.api.model.DefaultResponse
import com.mrkanapka.mrkanapkakotlin.api.model.DestinationDto
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_ui.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern
import com.mrkanapka.mrkanapkakotlin.api.model.RegisterRequest



class RegisterUI : AppCompatActivity() {

    private var emailInput: String = ""
    private var passwordInput: String = ""
    private var id_destination: Int = 0

    private val apiService by lazy {
        ApiClient.create()
    }
    private val disposables: CompositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_ui)

        disposables.add(
            apiService
                .fetchCities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.cities }
                .subscribe(
                    { handleFetchCitiesSuccess(it) },
                    { handleFetchCitiesError(it) }
                )
        )

        //przycisk rejestruj
        val button: Button = this.findViewById(R.id.register_button)

        button.setOnClickListener {
            register()
        }
    }

    //Funcka rejestracji
    private fun register(){
        if (validateEmail() && validatePassword())
        {
            // println("$emailInput $passwordInput $id_destination")
            //.instance.register(emailInput,passwordInput,id_destination.toString())
            apiService.register(RegisterRequest(emailInput, passwordInput, id_destination.toString()))
                .enqueue(object : Callback<DefaultResponse>{
                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        println("blad")
                    }

                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.body()?.message.equals("Client with that email already exists"))
                        {
                            Toast.makeText(applicationContext,"Podany email jest już zajęty", Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                            println("Haslo: " + passwordInput)
                            println("sukces")
                        }
                    }
                })
        }
    }

    //spinnery
    private fun handleFetchCitiesError(throwable: Throwable?) {
    }

    private fun handleFetchCitiesSuccess(cities: List<CityDto>) {
        val myCities = ArrayList<String>()
        //myCities.add("Wybierz miasto:")
        for(item in cities) {
            Log.i("miasto: ", item.city)
            myCities.add(item.city!!)
        }
        setCitySpinner(myCities)
        //setOfficeSpinner()

    }

    private fun setCitySpinner(cities: ArrayList<String>) {
        val citySpinner: Spinner = findViewById(R.id.city_register)
        var adapter = ArrayAdapter(this, R.layout.spinner_item, cities)
        citySpinner.adapter = adapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = adapter.getItem(position)
                println(item)

                disposables.add(
                    apiService
                        .fetchDestinations("destinations/" + cities[position])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { it.destinations }
                        .subscribe(
                            { handleFetchDestinationsSuccess(it) },
                            { handleFetchDestinationsError(it) }
                        )
                )
            }
        }
    }

    private fun handleFetchDestinationsError(throwable: Throwable) {
    }

    private fun handleFetchDestinationsSuccess(destinations: List<DestinationDto>) {

        val myDestination = ArrayList<String>()
        //myDestination.add("Wybierz biurowiec:")
        for(item in destinations) {
            Log.i("miasto: ", item.name + " " + item.street + " " + item.house_number)
            myDestination.add(item.name + " " + item.street + " " + item.house_number)
        }

        setOfficeSpinner(myDestination, destinations)

    }

    private fun setOfficeSpinner(myDestination: ArrayList<String>, destinations: List<DestinationDto>) {
        val citySpinner: Spinner = findViewById(R.id.office_register)
        var adapter = ArrayAdapter(this, R.layout.spinner_item, myDestination)
        citySpinner.adapter = adapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = adapter.getItem(position)
                id_destination = destinations[position].id_destination

            }
        }
    }

    //Walidacje
    private fun validateEmail(): Boolean {
        emailInput = email_register.text.toString().trim()

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
        passwordInput = password_register.text.toString().trim()

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
