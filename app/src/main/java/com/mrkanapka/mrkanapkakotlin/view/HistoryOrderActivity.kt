package com.mrkanapka.mrkanapkakotlin.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mrkanapka.mrkanapkakotlin.HistoryDetail
import com.mrkanapka.mrkanapkakotlin.LoginUI
import com.mrkanapka.mrkanapkakotlin.Main2Activity
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestHistory
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestToken
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseHistory
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseHistoryList
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import com.mrkanapka.mrkanapkakotlin.view.list.HistoryOrderListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_history_order.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryOrderActivity : AppCompatActivity() {

    private val adapter: FastItemAdapter<HistoryOrderListItem> = FastItemAdapter()

    private val apiService by lazy{
        ApiClient.create()
    }


    private val disposables: CompositeDisposable = CompositeDisposable()

    private val tokenManager by lazy {
        TokenManager()
    }

    private var accessToken : String = ""

    companion object {
        @SuppressLint("StaticFieldLeak")
        var pa: Activity? = null
    }

    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_order)

        val actionBar = supportActionBar
        actionBar!!.title = "Historia zamówień"
        actionBar.setDisplayHomeAsUpEnabled(true)

        mHandler = Handler()
        swipeHistoryOrder.setOnRefreshListener {
            adapter.clear()
            mRunnable = Runnable{
                tokenManager
                    .getToken()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgress()}
                    .doFinally{ hideProgress()}
                    .subscribe(
                        this::handleTokenCacheSuccess,
                        this::handleTokenCacheError
                    )
                    .addTo(disposables)
            }
            mHandler.post(mRunnable)
        }

        tokenManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)

        pa = this

        initializeRecyclerView()
    }
    private fun showProgress() {
        swipeHistoryOrder.isRefreshing = true
    }

    private fun hideProgress() {
        swipeHistoryOrder.isRefreshing = false
    }

    private fun handleTokenCacheSuccess(token: TokenEntity) {
        accessToken = token.token
        println(token.token)
        apiService.checkToken(RequestToken(token.token))
            .enqueue(object : Callback<ResponseDefault> {
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    Log.e("Status: ", "Fail connection")
                    Toast.makeText(applicationContext, "Nie można wczytać danych.\n" +
                            "Sprawdź swoje połaczenie z internetem", Toast.LENGTH_LONG).show()

                }

                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if (response.code() == 200) //Good token
                    {
                        showHistory(token.token,0)
                    }
                    if (response.code() == 400) //Bad token
                    {
                        Toast.makeText(applicationContext, "Zalogowano się z innego urządzenia\nZaloguj się ponownie", Toast.LENGTH_LONG).show()
                        logout()
                    }
                }
            })

    }

    private fun logout() {
        val main = Intent(this, LoginUI::class.java)
        startActivity(main)
        finish()
        Main2Activity.menuActivity!!.finish()
    }

    private fun handleTokenCacheError(throwable: Throwable) {
        // Log an error.
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showHistory(token: String, index: Int){
        apiService.fetchHistory(RequestHistory(token, index))
            .enqueue(object : Callback<ResponseHistory<List<ResponseHistoryList>>>{
                override fun onFailure(call: Call<ResponseHistory<List<ResponseHistoryList>>>, t: Throwable) {
                    Toast.makeText(applicationContext, "Nie można wczytać danych.\nSprawdź swoje połaczenie z internetem", Toast.LENGTH_LONG).show()
                    progressBarHistory.visibility = View.GONE
                    loading = true
                }

                override fun onResponse(
                    call: Call<ResponseHistory<List<ResponseHistoryList>>>,
                    response: Response<ResponseHistory<List<ResponseHistoryList>>>
                ) {
                    showHistory2(response.body()!!.orders)
                    response.body()!!.orders.lastIndex
                    progressBarHistory.visibility = View.GONE
                    page = response.body()!!.next
                }

            })
    }

    private fun showHistory2(films: List<ResponseHistoryList>) {
        // Log the fact.
        // Convert to list items.
        val items = films.map {
            HistoryOrderListItem(it)
        }
        // Display result.

        adapter.add(items)
        loading = true
    }

    var loading = true
    var page = 0

    private fun initializeRecyclerView() {
        recyclerViewHistory.itemAnimator = DefaultItemAnimator()
        recyclerViewHistory.adapter = adapter
        val mLayoutManager: LinearLayoutManager
        mLayoutManager = LinearLayoutManager(this)
        recyclerViewHistory.setLayoutManager(mLayoutManager)
        var pastVisiblesItems: Int
        var visibleItemCount: Int
        var totalItemCount: Int

        adapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }

        recyclerViewHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                //check for scroll down
                {
                    visibleItemCount = mLayoutManager.childCount
                    totalItemCount = mLayoutManager.itemCount
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()

                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            Log.v("...", "Ostatni item!")
                            Log.v("...", page.toString())
                            Log.v("...", totalItemCount.toString())
                            if (page != -1)
                            {
                                progressBarHistory.visibility = View.VISIBLE
                                showHistory(accessToken, page)
                            }

                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        })
    }

    private fun onItemClicked(item: HistoryOrderListItem): Boolean {

        // Retrieve model.
        val itemHistory = item.model

        val historyDetail = Intent(this, HistoryDetail::class.java)
        historyDetail.putExtra("orderNumber", itemHistory.order_number)
        historyDetail.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(historyDetail)
        return true
    }

}
