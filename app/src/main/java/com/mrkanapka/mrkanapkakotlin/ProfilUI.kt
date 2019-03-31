package com.mrkanapka.mrkanapkakotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.*
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestProfileEdit
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestToken
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseProfile
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

    private var access_token : String = ""

    private var flag : Boolean = false

    private var id_destination: Int = 0

    private var id_destination_profile: Int = 0

    private var id_city_profile: Int = 0

    private lateinit var progressDialog: AlertDialog

    private lateinit var dialog: AlertDialog

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

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.textDialog)
        message.text = "Sprawdzanie danych"
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()
        progressDialog.show()

        tokenManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)
    }

    private fun handleTokenCacheSuccess(token: TokenEntity) {
        access_token = token.token

        apiService.checkToken(RequestToken(access_token))
            .enqueue(object : Callback<ResponseDefault>{
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    Log.e("Status: ", "Fail connection")
                    progressDialog.cancel()
                    Toast.makeText(applicationContext, "Błąd połączenia z serwerem, spróbuj ponownie później", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if (response.code() == 200) { //Good token

                        getProfileData(token.token)
                        progressDialog.cancel()
                        Log.e("Status: ", "Good token")

                        edit_profile.setOnClickListener{
                            if(hasNetwork(applicationContext)) {
                                editMode()
                            } else {
                                Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                            }
                        }

                        edit_profile_accept.setOnClickListener{
                            if(hasNetwork(applicationContext)) {
                                updateProfile()
                            } else {
                                Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                            }
                        }

//                        edit_profile_cancel.setOnClickListener{
//                            confirmCancel()
//                        }

                        logoutButton.setOnClickListener{
                            Completable.fromAction {
                                AndroidDatabase.database
                                    .tokenDao()
                                    .removeToken()
                            }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    // data updated
                                }
                            logout()
                        }
                        Log.i("ID: ", "z response $id")
                    }
                    if (response.code() == 400) {//Bad token
                        logout()
                    }
                }
            })
    }

    private fun handleTokenCacheError(throwable: Throwable) {

        // Log an error.
    }

    private fun editMode() {
        edit_profile.visibility = View.GONE
        edit_profile_accept.visibility = View.VISIBLE
//        edit_profile_cancel.visibility = View.VISIBLE
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
        //spinners
        profile_city_spinner.isEnabled = true
        profile_office_spinner.isEnabled = true
        flag = true
    }

    private fun updateProfile(){
        editProfile(access_token)
        flag = false
    }

    private fun cancelEdit(){
//        edit_profile_cancel.visibility = View.GONE
        edit_profile_accept.visibility = View.GONE
        edit_profile.visibility = View.VISIBLE
        profile_email.setTextColor(Color.BLACK)
        profile_firstname.setText("")
        profile_firstname.hideKeyboard()
        profile_lastname.setText("")
        profile_lastname.hideKeyboard()
        profile_phone.setText("")
        profile_phone.hideKeyboard()
        profile_city_spinner.isEnabled = false
        profile_office_spinner.isEnabled = false
        getProfileData(access_token)
        flag = false
    }

    fun confirmCancel() {

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.confirm_cancel_popup, null)
        val buttonNo  = dialogLayout.findViewById<Button>(R.id.confirm_no)
        val buttonYes  = dialogLayout.findViewById<Button>(R.id.confirm_yes)

        buttonYes.setOnClickListener{
            dialog.cancel()
            cancelEdit()
        }

        buttonNo.setOnClickListener{
            dialog.cancel()
        }

        builder.setView(dialogLayout)
        dialog = builder.create()
        dialog.show()
    }

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

    var id2 = 0
    var flag2 = false
    private fun handleFetchCitiesSuccess(cities: List<CityDto>) {
        val myCities = ArrayList<String>()
        val myCitiesInt = ArrayList<Int>()
        //myCities.add("Wybierz miasto:")
        for(item in cities) {
            Log.i("miasto: ", item.city.toString())
            Log.i("Id z item city: ", item.id_city.toString())
            Log.i("Id z get city: ", id_city_profile.toString())
            myCitiesInt.add(item.id_city!!)
            myCities.add(item.city!!)
            if (id_city_profile == item.id_city){
                flag2 = true
            }
            else if (!flag2){
                id2++
            }
            Log.i("ID: ", "do ustawienia $id2")
        }
        setCitySpinner(myCities,myCitiesInt)
        Log.i("ID: ", "do ustawienia $id2")
        profile_city_spinner.setSelection(id2)
        Log.i("Ustawiłem ", "na id:  $id2")
        id2 = 0
        Log.i("ID: ", "$id2")
        flag2 = false
        //setOfficeSpinner()

    }
    private fun setCitySpinner(cities: ArrayList<String>, citiesInt: ArrayList<Int>) {
        val citySpinner: Spinner = findViewById(R.id.profile_city_spinner)
        val adapter = ArrayAdapter(this, R.layout.spinner_profile, cities)
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

    var id = 0
    var flagspinner = false
    private fun handleFetchDestinationsSuccess(destinations: List<DestinationDto>) {

        progressDialog.cancel()
        val myDestination = ArrayList<String>()
        //myDestination.add("Wybierz biurowiec:")
        for(item in destinations) {
            Log.i("Id z item: ", item.id_destination.toString())
            Log.i("Id z get ?: ", id_destination_profile.toString())
            myDestination.add(item.name + " " + item.street + " " + item.house_number)
            if (id_destination_profile == item.id_destination){
                flagspinner = true
            }
            else if (!flagspinner){
                id++
            }
            Log.i("ID: ", "do ustawienia $id")

        }
        setOfficeSpinner(myDestination, destinations)
        Log.i("ID: ", "do ustawienia $id")
        val officeSpinner: Spinner = findViewById(R.id.profile_office_spinner)
        if (destinations.lastIndex < id) {
            officeSpinner.setSelection(destinations.lastIndex - destinations.lastIndex)
        }
        else{
            officeSpinner.setSelection(id)
        }

        Log.i("Ustawiłem ", "na id:  $id")
        id = 0
        Log.i("ID: ", "$id")
        flagspinner = false
    }

    private fun setOfficeSpinner(myDestination: ArrayList<String>, destinations: List<DestinationDto>) {
        val officeSpinner: Spinner = findViewById(R.id.profile_office_spinner)
        val adapter = ArrayAdapter(this, R.layout.spinner_profile, myDestination)
        officeSpinner.adapter = adapter
        officeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    Toast.makeText(applicationContext, "Wystąpił błąd. Spróbuj ponownie", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ResponseProfile>, response: Response<ResponseProfile>) {
                    Log.e("Wiadomość: ", "Pobrane")
                    profile_email.setText(response.body()!!.email)

                    if(response.body()?.first_name == null || response.body()!!.first_name.equals("NULL")) {
                        profile_firstname.visibility = View.GONE
                        line3.visibility = View.GONE
                        imie.visibility = View.GONE
                    }
                    else {
                        profile_firstname.visibility = View.VISIBLE
                        profile_firstname.isEnabled = false
                        line3.visibility = View.VISIBLE
                        imie.visibility = View.VISIBLE
                        profile_firstname.setText(response.body()?.first_name)
                    }

                    if(response.body()?.last_name == null || response.body()!!.last_name.equals("NULL")) {
                        profile_lastname.visibility = View.GONE
                        line5.visibility = View.GONE
                        nazwisko.visibility = View.GONE
                    }
                    else {
                        profile_lastname.visibility = View.VISIBLE
                        profile_lastname.isEnabled = false
                        line5.visibility = View.VISIBLE
                        nazwisko.visibility = View.VISIBLE
                        profile_lastname.setText(response.body()!!.last_name)
                    }

                    if(response.body()?.telephone == null || response.body()!!.telephone.equals("NULL")) {
                        profile_phone.visibility = View.GONE
                        line7.visibility = View.GONE
                        phone.visibility = View.GONE
                    }
                    else {
                        profile_phone.visibility = View.VISIBLE
                        profile_phone.isEnabled = false
                        line7.visibility = View.VISIBLE
                        phone.visibility = View.VISIBLE
                        profile_phone.setText(response.body()!!.telephone)
                    }
                    id_destination_profile = response.body()!!.id_destination
                    id_city_profile = response.body()!!.id_city
                    profile_city_spinner.isEnabled = false
                    profile_office_spinner.isEnabled = false

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
                }
            })
    }

    private fun editProfile(access_token: String) {
        var nameInput: String = profile_firstname.text.toString().trim()
        var lastnameInput: String = profile_lastname.text.toString().trim()
        var emailInput : String = profile_email.text.toString().trim()
        var phoneInput : String = profile_phone.text.toString().trim()

        if (nameInput.equals("")) {
            nameInput = "NULL"
            Log.e("Name: ", nameInput)
        }
        if(lastnameInput.equals("")) {
            lastnameInput = "NULL"
        }
        if(phoneInput.equals("")) {
            phoneInput = "NULL"
        }

        Log.e("ID: ",id_destination.toString())
        apiService.editProfile(
            RequestProfileEdit(
                access_token,
                emailInput,
                nameInput,
                id_destination,
                lastnameInput,
                phoneInput
            )
        )
            .enqueue(object : Callback<ResponseDefault>{
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    cancelEdit()
                    Toast.makeText(applicationContext, "Wystąpił błąd. Spróbuj ponownie", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if(response.code()== 200) {
                        Log.e("Wiadomość: ", "Sukces pomyślnie wysłano")
//                        edit_profile_cancel.visibility = View.GONE
                        edit_profile_accept.visibility = View.GONE
                        edit_profile.visibility = View.VISIBLE
                        profile_email.setTextColor(Color.BLACK)
                        profile_firstname.hideKeyboard()
                        profile_lastname.hideKeyboard()
                        profile_phone.hideKeyboard()
                        getProfileData(access_token)
                    }
                }
            })
    }



    //Dziala z każdym edittextem
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    //Działa z api26 z StackOverflow
    //Działa globalnie dla danego okna
    private fun hideKeyboard() {
        val view: View = if (currentFocus == null) View(this) else currentFocus
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun logout() {
        val main = Intent(this, LoginUI::class.java)
        startActivity(main)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (flag) {
            confirmCancel()
            flag = false
        }
        else {
            val intent = Intent(this, Main2Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun hasNetwork(context: Context): Boolean {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected!!
    }
}
