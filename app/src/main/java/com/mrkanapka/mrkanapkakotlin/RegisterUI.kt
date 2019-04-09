package com.mrkanapka.mrkanapkakotlin


import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.CityDto
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.api.model.DestinationDto
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_ui.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestRegister
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase.Companion.database
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import io.reactivex.Completable
import java.time.LocalDateTime


class RegisterUI : AppCompatActivity() {

    private var emailInput: String = ""
    private var passwordInput: String = ""
    private var id_destination: Int = 0
    private var registrationID: String = ""

    private val apiService by lazy {
        ApiClient.create()
    }
    private val disposables: CompositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_ui)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                registrationID = task.result!!.token




                Log.e("...", registrationID)
            })

        val myCities = ArrayList<String>()
        myCities.add("Miasto")
        setCitySpinnerOffline(myCities)

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
        val main = Intent(this, Main2Activity::class.java)


        if (validateEmail() && validatePassword() && validateCheckbox())
        {
            // println("$emailInput $passwordInput $id_destination")
            //.instance.register(emailInput,passwordInput,id_destination.toString())
            apiService.register(
                RequestRegister(
                    emailInput,
                    passwordInput,
                    id_destination.toString(),
                    registrationID

                )
            )
                .enqueue(object : Callback<ResponseDefault>{
                    override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                        Toast.makeText(applicationContext, "Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()

                    }

                    @SuppressLint("CheckResult")
                    override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                        if (response.code() == 202)
                        {
                            Toast.makeText(applicationContext,"Podany email jest już zajęty", Toast.LENGTH_LONG).show()
                        }
                        else if (response.code() == 201)
                        {
                            Completable.fromAction {
                                database
                                    .tokenDao()
                                    .removeAndInsert(TokenEntity(response.body()!!.message, id_destination))
                            }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    // data updated
//                                }

//                            FirebaseMessaging.getInstance().subscribeToTopic(id_destination.toString())
//                                .addOnCompleteListener { task ->
//                                    var msg = id_destination.toString()
//                                    if (!task.isSuccessful) {
//                                        msg = "blad"
//                                    }
//                                    Log.d(TAG, msg)
//                                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                }
                            startActivity(main)
                            finish()
                        } else
                        {
                            Toast.makeText(applicationContext, "Wystąpił bład. Spróbuj ponownie później", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }

    //spinnery
    private fun handleFetchCitiesError(throwable: Throwable?) {
        Handler().postDelayed({
            //Log.e("Czas",LocalDateTime.now().toString())
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
        }, 3000)

    }

    private fun handleFetchCitiesSuccess(cities: List<CityDto>) {
        val myCities = ArrayList<String>()
        val myCitiesInt = ArrayList<Int>()
        //myCities.add("Wybierz miasto:")
        for(item in cities) {
            Log.i("miasto: ", item.city.toString())
            myCitiesInt.add(item.id_city!!)
            myCities.add(item.city!!)
        }
        setCitySpinner(myCities,myCitiesInt)
        //setOfficeSpinner()

    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginUI::class.java)
        startActivity(intent)
        finish()
    }

    private fun setCitySpinner(cities: ArrayList<String>, citiesInt: ArrayList<Int>) {
        val citySpinner: Spinner = findViewById(R.id.city_register)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, cities)
        citySpinner.adapter = adapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                disposables.add(
                    apiService
                        .fetchDestinations("destinations/" + citiesInt[position])
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
        val adapter = ArrayAdapter(this, R.layout.spinner_item, myDestination)
        citySpinner.adapter = adapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
        //"(?=.*[@#$%^&+=])" +    //at least 1 special character
        "(?=\\S+$)" +           //no white spaces
        ".{6,}" +               //at least 6 characters
        "$")

    private fun validatePassword(): Boolean {
        passwordInput = password_register.text.toString().trim()

        if (passwordInput.isEmpty()) {
            password_register.error = "Pole nie może być puste"
            return false
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            password_register.error = "Hasło musi zawierać od 6 do 32 znaków, jedną cyfrę, jedną wielką literę oraz jedną małą literę."
            return false
        } else {
            password_register.error = null
            return true
        }
    }

    private fun validateCheckbox(): Boolean {
        if(checkboxSelect.isChecked){
            return true
        }
        else {
            checkboxSelect.error = ""
            return false
        }
    }

    private fun setCitySpinnerOffline(cities: ArrayList<String>) {
        val citySpinner: Spinner = findViewById(R.id.city_register)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, cities)
        citySpinner.adapter = adapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }
    }
}
