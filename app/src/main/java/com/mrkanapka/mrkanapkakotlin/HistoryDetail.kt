package com.mrkanapka.mrkanapkakotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestHistory
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestHistoryDetail
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestToken
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseHistoryDetail
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseHistoryDetailProduct
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import com.mrkanapka.mrkanapkakotlin.view.HistoryOrderActivity
import com.mrkanapka.mrkanapkakotlin.view.list.CartListItem
import com.mrkanapka.mrkanapkakotlin.view.list.HistoryDetailListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_history_detail.*
import kotlinx.android.synthetic.main.activity_history_order.*
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.android.synthetic.main.item_menu.*
import kotlinx.android.synthetic.main.order_cancel_popup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryDetail : AppCompatActivity() {

    private val adapter: FastItemAdapter<HistoryDetailListItem> = FastItemAdapter()

    private val apiService by lazy {
        ApiClient.create()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    private val tokenManager by lazy {
        TokenManager()
    }

    private lateinit var cancelDialog: AlertDialog

    var accessToken = ""
    var orderNumber = ""

    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    companion object {
        var flag: Boolean = false
        @SuppressLint("StaticFieldLeak")
        var pa: Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)


        if(intent.extras != null)
        {
            for(key in intent?.extras!!.keySet())
            {
                Log.e("...", key)
                if(key.equals("orderNr"))
                {
                    orderNumber = intent.extras?.getString(key)!!
                }
                else if (key.equals("orderNumber"))
                {
                    orderNumber = intent.getStringExtra("orderNumber")
                }
            }
        }

        HistoryDetail.flag = false
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Zamówienie $orderNumber"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        pa = this

        tokenManager
            .getToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleTokenCacheSuccess,
                this::handleTokenCacheError
            )
            .addTo(disposables)

        mHandler = Handler()
        swipeHistoryOrderDetail.setOnRefreshListener {
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

        buttonCancelOrder.setOnClickListener{
            cancelPopup()
        }

        initializeRecyclerView()
    }

    private fun showProgress() {
        swipeHistoryOrderDetail.isRefreshing = true
    }

    private fun hideProgress() {
        swipeHistoryOrderDetail.isRefreshing = false
    }

    private fun logout() {
        val main = Intent(this, LoginUI::class.java)
        startActivity(main)
        finish()
        HistoryOrderActivity.pa!!.finish()
        Main2Activity.menuActivity!!.finish()

    }

    private fun handleTokenCacheSuccess(token: TokenEntity) {
        accessToken = token.token

        apiService.checkToken(RequestToken(token.token))
            .enqueue(object : Callback<ResponseDefault> {
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    Log.e("Status: ", "Fail connection")
                    Toast.makeText(applicationContext, "Brak internetu, tryb offline", Toast.LENGTH_LONG).show()

                }

                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if (response.code() == 200) //Good token
                    {
                        showDetail(token.token, orderNumber)
                    }
                    if (response.code() == 400) //Bad token
                    {
                        Toast.makeText(applicationContext, "Zalogowano się z innego urządzenia\nZaloguj się ponownie", Toast.LENGTH_LONG).show()
                        logout()
                    }
                }
            })
    }

    private fun handleTokenCacheError(throwable: Throwable) {
        // Log an error.
    }

    private fun showDetail(token : String, orderNumber: String) {
        apiService.fetchHistoryDetail(RequestHistoryDetail(token,orderNumber))
            .enqueue(object : Callback<ResponseHistoryDetail<List<ResponseHistoryDetailProduct>>>{
                override fun onFailure(
                    call: Call<ResponseHistoryDetail<List<ResponseHistoryDetailProduct>>>,
                    t: Throwable
                ) {
                    Toast.makeText(applicationContext, "Wystąpił błąd.\nSpróbuj ponownie później", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseHistoryDetail<List<ResponseHistoryDetailProduct>>>,
                    response: Response<ResponseHistoryDetail<List<ResponseHistoryDetailProduct>>>
                ) {
                    historyDetailNumberText.text = orderNumber
                    historyDateText.text = response.body()!!.date
                    historyOfficeText.text = response.body()!!.destination
                    historySellerText.text = response.body()!!.seller
                    historyStatusText.text = response.body()!!.name
                    historyTotalPriceText.text = "%.2f zł".format(response.body()!!.full_price)
                    fetchHistoryProducts(response.body()!!.products)
                    if ( response.body()!!.flag == 1)
                    {
                        buttonCancelOrder.visibility = View.VISIBLE
                    }
                }

            })
    }

    private fun fetchHistoryProducts(films: List<ResponseHistoryDetailProduct>) {
        // Log the fact.
        // Convert to list items.
        val items = films.map {
            HistoryDetailListItem(it)
        }

        // Display result.
        adapter.setNewList(items)
    }

    private fun initializeRecyclerView() {
        historyDetailRecyclerView.layoutManager = LinearLayoutManager(this)
        historyDetailRecyclerView.itemAnimator = DefaultItemAnimator()
        historyDetailRecyclerView.adapter = adapter
        adapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }

    }

    private fun onItemClicked(item: HistoryDetailListItem): Boolean {
        // Retrieve model.
        val product = item.model
        val foodDetail = Intent(this, FoodDetail::class.java)
        foodDetail.putExtra("intVariableName", product.id)
        foodDetail.putExtra("token", accessToken)
        foodDetail.putExtra("fromCart", 0)
        foodDetail.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(foodDetail)
        return true
    }

    //region cancelOrder

    private fun cancelPopup(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.order_cancel_popup, null)
        val buttonYes = dialogLayout.findViewById<Button>(R.id.cancelPopupYes)
        val buttonNo = dialogLayout.findViewById<Button>(R.id.cancelPopupNo)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        buttonNo.setOnClickListener{
            cancelDialog.cancel()
        }

        buttonYes.setOnClickListener{
            cancelOrder()
            cancelDialog.cancel()
        }
        builder.setView(dialogLayout)
        cancelDialog = builder.create()
        cancelDialog.show()
    }

    private fun cancelOrder(){
        apiService.checkToken(RequestToken(accessToken))
            .enqueue(object : Callback<ResponseDefault> {
                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                    Log.e("Status: ", "Fail connection")
                    Toast.makeText(applicationContext, "Brak internetu, tryb offline", Toast.LENGTH_LONG).show()

                }

                override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                    if (response.code() == 200) //Good token
                    {
                        apiService.cancelOrder(RequestHistoryDetail(accessToken,orderNumber))
                            .enqueue(object : Callback<ResponseDefault>{
                                override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                                    Toast.makeText(applicationContext, "Wystąpił błąd.\nSpróbuj ponownie później", Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(
                                    call: Call<ResponseDefault>,
                                    response: Response<ResponseDefault>
                                ) {
                                    historyStatusText.text = response.body()!!.id_status
                                    buttonCancelOrder.visibility = View.GONE
                                    HistoryOrderActivity.pa!!.finish()
                                    flag = true
                                }
                            })
                    }
                    if (response.code() == 400) //Bad token
                    {
                        Toast.makeText(applicationContext, "Zalogowano się z innego urządzenia\nZaloguj się ponownie", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    //endregion

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        Log.e("Flaga",flag.toString())
        if(flag){
            val intent = Intent(this, HistoryOrderActivity::class.java)
            startActivity(intent)
            finish()
            Log.e("...","otwieram na nowo")
        }
        else{
            Log.e("...","wykonuje sie normalnie")
            super.onBackPressed()
        }

    }
}
