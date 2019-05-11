package com.mrkanapka.mrkanapkakotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.messaging.FirebaseMessaging
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.CategoryDto
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestToken
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseProfile
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase.Companion.database
import com.mrkanapka.mrkanapkakotlin.database.entity.CategoryEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.SellerEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import com.mrkanapka.mrkanapkakotlin.view.CartActivity
import com.mrkanapka.mrkanapkakotlin.view.HistoryOrderActivity
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val apiService by lazy {
        ApiClient.create()
    }

    private val tokenManager by lazy {
        TokenManager()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var menuActivity: Activity? = null
    }

    private var token = ""

    private val disposables: CompositeDisposable = CompositeDisposable()

    private var boolCacheCategory = false

    private var boolCacheSellers= false

    private lateinit var categoryCache: List<CategoryEntity>

    private lateinit var sellersCache: List<SellerEntity>

    private var counterek = 0

    private fun handleFetchCategorySuccess( category: List<CategoryEntity>, id_seller: Int) {

        // Log the fact.
        Log.i("tabfragment", "Successfully fetched categories.")
        var different = false

        if (boolCacheCategory) {
            if (category.size != categoryCache.size) {
                println("rozmiar arrayList: " + category.size + " rozmiar tego drugiego: " + categoryCache.size)
                different = true
            }
            else {
                for ((index, item) in category.withIndex()) {
                    if (item.id_category != categoryCache[index].id_category && item.name != categoryCache[index].name)
                        different = true
                }
            }
        }

        if (different) {
            val arrayList = ArrayList<CategoryDto>()
            for (item in category)
                arrayList.add(CategoryDto(item.id_category, item.name))
            displayFragmentCategory(arrayList, id_seller)
        }
    }

    private fun handleFetchCategoryError(throwable: Throwable) {

        // Log an error.
        Log.e("tabfragment", "An error occurred while fetching categories.")
        Log.e("tabfragment", throwable.localizedMessage)
        //Snackbar.make(root, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
    }

    private fun handleFetchCategoryCacheSuccess( category: List<CategoryEntity>, id_seller: Int) {
        categoryCache = category
        // Log the fact.
        Log.i("tabfragment", "Successfully fetched categories.")
        val arrayList = ArrayList<CategoryDto>()
        for (item in category) {
            arrayList.add(CategoryDto(item.id_category, item.name))
        }
        displayFragmentCategory(arrayList, id_seller)

        boolCacheCategory = true

    }

    private fun handleFetchCategoryCacheError(throwable: Throwable) {

        // Log an error.
        Log.e("tabfragment", "An error occurred while fetching categories.")
        Log.e("tabfragment", throwable.localizedMessage)
        //Snackbar.make(root, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
    }

    private fun handleTokenCacheSuccess(token: TokenEntity) {
        this.token = token.token
        apiService.fetchProfile(RequestToken(token.token))
            .enqueue(object : Callback<ResponseProfile> {
                override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
                    print("blad")
                    downloadData(token.id_destination)
                }

                override fun onResponse(call: Call<ResponseProfile>, response: Response<ResponseProfile>) {
                    Log.e("Wiadomość: ", "Pobrane")
                    downloadData(response.body()!!.id_destination)

                }
            })

    }

    private fun downloadData(id_destination: Int) {
        //From cache

        println(id_destination)
        tokenManager
            .getSellers(id_destination) //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleFetchSellerCacheSuccess,
                this::handleFetchSellerCacheError
            )
            .addTo(disposables)

        //From api
        tokenManager
            .downloadSellers("seller/$id_destination", id_destination) //w domysle id_destination klienta ktore powinno byc pobierane z api włącznie z tokenem
            .andThen(tokenManager.getSellers(id_destination))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {  } //funkcje np progressbar show
            .doFinally {  } //funkcje np progressbar show
            .subscribe(
                this::handleFetchSellerSuccess,
                this::handleFetchSellerError
            )
            .addTo(disposables)
    }

    private fun handleTokenCacheError(throwable: Throwable) {

        // Log an error.
    }



    private fun handleFetchSellerSuccess(seller: List<SellerEntity>) {

        var different = false
        if (boolCacheSellers) {
            println("rozmiar arrayList: " + seller.size + " rozmiar tego drugiego: " + sellersCache.size)
            if (seller.size != sellersCache.size) {
                different = true
            }
            else {
                for ((index, item) in seller.withIndex()) {
                    if (item.id_seller != sellersCache[index].id_seller && item.sellername != sellersCache[index].sellername)
                        different = true
                }
            }
        }

        if (different)
        {
            val mySeller = ArrayList<String>()
            for (item in seller) {
                println(item)
                mySeller.add(item.sellername)
            }

            setSellerSpinner(mySeller, seller)
        }
    }

    private fun handleFetchSellerError(throwable: Throwable?) {

    }

    private fun handleFetchSellerCacheSuccess(seller: List<SellerEntity>) {
        sellersCache = seller
        val mySeller = ArrayList<String>()
        for (item in seller) {
            println(item)
            mySeller.add(item.sellername)
        }

        setSellerSpinner(mySeller, seller)

        boolCacheSellers = true


    }

    private fun handleFetchSellerCacheError(throwable: Throwable?) {

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        tokenManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //domyslny ekran czyli odpali się default

    }

    private fun setSellerSpinner(mySeller: ArrayList<String>, seller: List<SellerEntity>) {
        val sellerSpinner: Spinner = findViewById(R.id.spinner_seller)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, mySeller)
        sellerSpinner.adapter = adapter
        sellerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //id_destination = destinations[position].id_destination

                //From cache
                tokenManager
                    .getCategory(seller[position].id_seller)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { handleFetchCategoryCacheSuccess(it, seller[position].id_seller) },
                        { handleFetchCategoryCacheError(it) }
                    )
                    .addTo(disposables)

                //From api
                tokenManager
                    .downloadCategory("products/seller/" + seller[position].id_seller, seller[position].id_seller)
                    .andThen(tokenManager.getCategory(seller[position].id_seller))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {  } //funkcje np progressbar show
                    .doFinally {  } //funkcje np progressbar show
                    .subscribe(
                        { handleFetchCategorySuccess(it, seller[position].id_seller) },
                        { handleFetchCategoryError(it) }
                    )
                    .addTo(disposables)

            }
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //Ikona koszyka
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_cart -> {
                val intent = Intent(this, CartActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return super.onOptionsItemSelected(item)}
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun displayFragmentCategory(
        category: ArrayList<CategoryDto>,
        id_seller: Int
    ) {
        counterek++
        println(counterek)
        val fragment  = TabFragment.newInstance(category, token, id_seller)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.relativeLayout, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (item.itemId){
            R.id.action_cart ->  {
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }
            R.id.logout ->  {
                apiService.logout(RequestToken(token))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeBy(
                        onSuccess = {
                            Log.e("...",it.message)
                            Completable.fromAction {
                                AndroidDatabase.database
                                    .tokenDao()
                                    .removeToken()
                            }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    // data updated
                                }
                            val intent = Intent(this, LoginUI::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onError = {
                            if (it is HttpException)
                                Toast.makeText(applicationContext,it.message(), Toast.LENGTH_LONG).show()
                            else
                                Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                        }
                    )
            }
            R.id.profile -> {
                if (this.hasNetwork(applicationContext)!!){
                    val intent = Intent(this, ProfilUI::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(applicationContext,"Sprawdź połączenie z internetem", Toast.LENGTH_LONG).show()
                }
            }
            R.id.history ->{
                val intent = Intent(this, HistoryOrderActivity::class.java)
                startActivity(intent)
            }
            else -> {


            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }
}