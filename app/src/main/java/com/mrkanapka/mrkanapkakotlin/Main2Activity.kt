package com.mrkanapka.mrkanapkakotlin

import android.content.Intent
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
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.CategoryDto
import com.mrkanapka.mrkanapkakotlin.api.model.SellerDto
import com.mrkanapka.mrkanapkakotlin.database.AndroidDatabase.Companion.database
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import com.mrkanapka.mrkanapkakotlin.view.CartActivity
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val apiService by lazy {
        ApiClient.create()
    }

    private val tokenManager by lazy {
        TokenManager()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    private fun handleFetchCategorySuccess(category: List<CategoryDto>) {

        // Log the fact.
        Log.i("tabfragment", "Successfully fetched categories.")


        val arrayList = ArrayList<CategoryDto>()
        for (item in category) {
            arrayList.add(item)
        }

        displayScreen(R.id.main_menu, arrayList)
        dialog.cancel()
    }

    private fun handleFetchCategoryError(throwable: Throwable) {

        // Log an error.
        Log.e("tabfragment", "An error occurred while fetching categories.")
        Log.e("tabfragment", throwable.localizedMessage)
        dialog.cancel()
        //Snackbar.make(root, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
    }

    private fun handleTokenCacheSuccess(token: TokenEntity) {


    }

    private fun handleTokenCacheError(throwable: Throwable) {

        // Log an error.
    }

    private fun handleFetchSellerSuccess(seller: List<SellerDto>) {

        val mySeller = ArrayList<String>()
        for (item in seller) {
            println(item)
            mySeller.add(item.sellername!!)
        }

        setSellerSpinner(mySeller, seller)
        dialog.cancel()

    }

    private fun handleFetchSellerError(throwable: Throwable?) {
        dialog.cancel()
    }

    private fun setSellerSpinner(mySeller: ArrayList<String>, seller: List<SellerDto>) {
        val sellerSpinner: Spinner = findViewById(R.id.spinner_seller)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, mySeller)
        sellerSpinner.adapter = adapter
        sellerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //id_destination = destinations[position].id_destination
                dialog.show()
                disposables.add(
                    apiService
                        .fetchCategory("products/seller/" + seller[position].id_seller)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { it.category }
                        .subscribe(
                            { handleFetchCategorySuccess(it) },
                            { handleFetchCategoryError(it) }
                        )
                )

            }
        }
    }

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.textDialog)
        message.text = "Pobieranie danych..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

////      Funkcja od ikonki maila
//        fab.setOnClickListener { //view ->
////            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                .setAction("Action", null).show()
//            val intent = Intent(this, CartActivity::class.java)
//            startActivity(intent)
//        }


        disposables.add(
            apiService
                .fetchSellers("seller/" + 1) //tutaj w domysle id_destination wybrane w profilu!!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.seller }
                .subscribe(
                    { handleFetchSellerSuccess(it) },
                    { handleFetchSellerError(it) }
                )
        )


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
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                return super.onOptionsItemSelected(item)}
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun displayScreen(id: Int, category: ArrayList<CategoryDto>) {
        val fragment  = when (id) {
            R.id.main_menu -> {
                // TU CHYBA PROBLEM
                TabFragment.newInstance(category)
            }
            else -> {
                TabFragment.newInstance(category)
            }
        }
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.logout ->  {
                Completable.fromAction {
                    database
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
            }
            R.id.profile -> {
                val intent = Intent(this, ProfilUI::class.java)
                startActivity(intent)
            }
            else -> {


            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}