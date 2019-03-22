package com.mrkanapka.mrkanapkakotlin.view

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.RequestToken
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseCart
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseCartDetail
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import com.mrkanapka.mrkanapkakotlin.view.list.CartListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_order_summary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OrderSummaryActivity : AppCompatActivity() {

    private val adapter: FastItemAdapter<CartListItem> = FastItemAdapter()

    private val apiService by lazy {
        ApiClient.create()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    private val tokenManager by lazy {
        TokenManager()
    }

    private var accessToken : String = ""

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        val actionBar = supportActionBar
        actionBar!!.title = "Podsumowanie zamówienia"
        actionBar.setDisplayHomeAsUpEnabled(true)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        datapicker_button.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                date_textView.setText(""+ mDay + "/" + mMonth + "/" + mYear)
            }, year, month, day)

            dpd.show()
        }

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
        accessToken = token.token
        println(token.token)
        apiService.checkToken(RequestToken(token.token))
            .enqueue(object : Callback<ResponseDefault> {
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    Log.e("Status: ", "Fail connection")
                    Toast.makeText(applicationContext, "Brak internetu, tryb offline", Toast.LENGTH_LONG).show()

                }

                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if (response.code() == 200) //Good token
                    {
                        showCart(token.token)
                    }
                    if (response.code() == 400) //Bad token
                    {
                        Toast.makeText(applicationContext, "Zalogowano się z innego urządzenia\nZaloguj się ponownie", Toast.LENGTH_LONG).show()
                    }
                }
            })


    }

    private fun handleFetchCartSuccess(films: List<ResponseCartDetail>) {
        // Log the fact.
        // Convert to list items.
        val items = films.map {
            CartListItem(it)
        }

        // Display result.
        adapter.setNewList(items)
    }

    private fun handleFetchCartError(throwable: Throwable) {
        // Log an error.
    }

    private fun handleTokenCacheError(throwable: Throwable) {
        // Log an error.
    }


    private fun initializeRecyclerView() {

        // Convert to list items.
        //items = Cart.infoItem().map { CartListItem(it) }.toMutableList()

//        if (items.size != 0){
//            textView3.visibility = View.GONE
//            imageView2.visibility = View.GONE
//        }
//        else {
//            textView3.visibility = View.VISIBLE
//            imageView2.visibility = View.VISIBLE
//        }
//
//
//        adapter.setNewList(items)
        //TU TRZEBA BYŁO ZMIENIC THIS NA CONTEXT
        summaryRecyclerView.layoutManager = LinearLayoutManager(this)
        summaryRecyclerView.itemAnimator = DefaultItemAnimator()
        summaryRecyclerView.adapter = adapter
    }
    private fun showCart(token : String){
        apiService.fetchCart(RequestToken(token))
            .enqueue(object : Callback<ResponseCart<List<ResponseCartDetail>>>{
                override fun onFailure(
                    call: Call<ResponseCart<List<ResponseCartDetail>>>,
                    t: Throwable
                ) {
                    Toast.makeText(applicationContext, "Wystąpił błąd.\nSpróbuj ponownie później", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseCart<List<ResponseCartDetail>>>,
                    response: Response<ResponseCart<List<ResponseCartDetail>>>
                ) {
                    handleFetchCartSuccess(response.body()!!.cart)
                    initializeRecyclerView()
                }
            })
    }
}
