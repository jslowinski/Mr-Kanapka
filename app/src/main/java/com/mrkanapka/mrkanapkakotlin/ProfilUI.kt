package com.mrkanapka.mrkanapkakotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.*
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.TokenCheck
import com.mrkanapka.mrkanapkakotlin.api.model.*
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_profil_ui.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime


class ProfilUI : AppCompatActivity() {

    private val apiService by lazy {
        ApiClient.create()
    }
    private val disposables: CompositeDisposable = CompositeDisposable()

    private val database by lazy {
        AndroidDatabase.database
    }

    private val tokenManager by lazy {
        TokenManager()
    }

    private val session by lazy {
        TokenCheck
    }

    private var access_token : String = ""

    private var flag : Boolean = false

    private var id_destination: Int = 0

    private var id_destination_profile: Int = 0

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_ui)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Profil"
        setSupportActionBar(toolbar)
        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)


//            session.sessionStatus()
//        if (!session.sessionStatus())
//        {
//            Completable.fromAction {
//                AndroidDatabase.database
//                    .tokenDao()
//                    .removeToken()
//            }.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    // data updated
//                }

        tokenManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)





    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleTokenCacheSuccess(token: TokenEntity) {
        access_token = token.token

        apiService.checkToken(RequestToken(access_token))
            .enqueue(object : Callback<ResponseDefault>{
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    Log.e("Status: ", "Fail connection")
                }

                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if (response.code() == 200) //Good token
                    {
                        Log.e("Status: ", "Good token")
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

                        getProfileData(token.token)
                        val edit_profile = findViewById<ImageView>(R.id.edit_profile)
                        val edit_profile_accept = findViewById<ImageView>(R.id.edit_profile_accept)
                        val edit_profile_cancel = findViewById<ImageView>(R.id.edit_profile_cancel)

                        val button : Button = findViewById(R.id.profil)

                        button.setOnClickListener{
                            Toast.makeText(applicationContext, access_token, Toast.LENGTH_LONG).show()
                        }

                        edit_profile.setOnClickListener{
                            editMode()
                        }

                        edit_profile_accept.setOnClickListener{
                            updateProfile()
                        }


                    }
                    if (response.code() == 204) //Bad token
                    {
                        Log.e("Status: ", "Bad")


                    }
                }
            })



    }

    private fun handleTokenCacheError(throwable: Throwable) {

        // Log an error.
    }

    private fun editMode()
    {
        edit_profile.visibility = View.GONE
        edit_profile_accept.visibility = View.VISIBLE
        edit_profile_cancel.visibility = View.VISIBLE
        //edycja imienia
        profile_firstname.visibility = View.VISIBLE
        profile_firstname.isEnabled = true
        line3.visibility = View.VISIBLE
        imie.visibility = View.VISIBLE
        //edycja nazwiska
        profile_lastname.visibility = View.VISIBLE
        profile_lastname.isEnabled = true
        line5.visibility = View.VISIBLE
        nazwisko.visibility = View.VISIBLE
        //edycja telefonu
        profile_phone.visibility = View.VISIBLE
        profile_phone.isEnabled = true
        line7.visibility = View.VISIBLE
        phone.visibility = View.VISIBLE
        //mail
        profile_email.setTextColor(Color.rgb(103,98,94))
    }

    private fun updateProfile(){
        editProfile(access_token)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleFetchCitiesError(throwable: Throwable?) {
        Handler().postDelayed({
            Log.e("Czas", LocalDateTime.now().toString())
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
        //myCities.add("Wybierz miasto:")
        for(item in cities) {
            Log.i("miasto: ", item.city)
            myCities.add(item.city!!)
        }
        setCitySpinner(myCities)
        //setOfficeSpinner()

    }
    private fun setCitySpinner(cities: ArrayList<String>) {
        val citySpinner: Spinner = findViewById(R.id.profile_city_spinner)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, cities)

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
        val citySpinner: Spinner = findViewById(R.id.profile_office_spinner)
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

    private fun getProfileData(access_token : String){

        apiService.fetchProfile(RequestToken(access_token))
            .enqueue(object : Callback<ResponseProfile>{
                override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
                    print("blad")
                }

                override fun onResponse(call: Call<ResponseProfile>, response: Response<ResponseProfile>) {
                    Log.e("Wiadomość: ", "Pobrane")
                    profile_email.setText(response.body()!!.email)
                    if(response.body()?.first_name == null || response.body()!!.first_name.equals("NULL"))
                    {
                        profile_firstname.visibility = View.GONE
                        line3.visibility = View.GONE
                        imie.visibility = View.GONE
                    }
                    else
                    {
                        profile_firstname.visibility = View.VISIBLE
                        profile_firstname.isEnabled = false
                        line3.visibility = View.VISIBLE
                        imie.visibility = View.VISIBLE
                        profile_firstname.setText(response.body()?.first_name)
                    }
                    if(response.body()?.last_name == null || response.body()!!.last_name.equals("NULL"))
                    {
                        profile_lastname.visibility = View.GONE
                        line5.visibility = View.GONE
                        nazwisko.visibility = View.GONE
                    }
                    else
                    {
                        profile_lastname.visibility = View.VISIBLE
                        profile_lastname.isEnabled = false
                        line5.visibility = View.VISIBLE
                        nazwisko.visibility = View.VISIBLE
                        profile_lastname.setText(response.body()!!.last_name)
                    }

                    if(response.body()?.telephone == null || response.body()!!.telephone.equals("NULL"))
                    {
                        profile_phone.visibility = View.GONE
                        line7.visibility = View.GONE
                        phone.visibility = View.GONE
                    }
                    else
                    {

                        profile_phone.visibility = View.VISIBLE
                        profile_phone.isEnabled = false
                        line7.visibility = View.VISIBLE
                        phone.visibility = View.VISIBLE
                        profile_phone.setText(response.body()!!.telephone)
                    }
                    id_destination_profile = response.body()!!.id_destination
//                    Toast.makeText(applicationContext, response.body()!!.email, Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun editProfile(access_token: String) {
        var nameInput: String = profile_firstname.text.toString().trim()
        var lastnameInput: String = profile_lastname.text.toString().trim()
        var emailInput : String = profile_email.text.toString().trim()
        var phoneInput : String = profile_phone.text.toString().trim()

        if (nameInput.equals(""))
        {
            nameInput = "NULL"
            Log.e("Name: ", nameInput)
        }
        if(lastnameInput.equals(""))
        {
            lastnameInput = "NULL"
        }
        if(phoneInput.equals(""))
        {
            phoneInput = "NULL"
        }
        apiService.editProfile(RequestProfileEdit(access_token,emailInput,nameInput,id_destination_profile,lastnameInput,phoneInput))
            .enqueue(object : Callback<ResponseDefault>{
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if(response.code()== 200)
                    {
                        Log.e("Wiadomość: ", "Sukces pomyślnie wysłano")
                        edit_profile_cancel.visibility = View.GONE
                        edit_profile_accept.visibility = View.GONE
                        edit_profile.visibility = View.VISIBLE
                        profile_email.setTextColor(Color.BLACK)
                        getProfileData(access_token)
                    }
                }

            })

    }
}
