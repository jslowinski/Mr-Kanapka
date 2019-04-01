package com.mrkanapka.mrkanapkakotlin.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mrkanapka.mrkanapkakotlin.HistoryDetail
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestOrder
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestToken
import com.mrkanapka.mrkanapkakotlin.api.model.Response.*
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import com.mrkanapka.mrkanapkakotlin.view.list.SummaryListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_order_summary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OrderSummaryActivity : AppCompatActivity() {

    private val adapter: FastItemAdapter<SummaryListItem> = FastItemAdapter()

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
    private var dayS: String = ""
    private var monthS: String = ""
    private var yearS: String = ""

    private lateinit var orderDialog: AlertDialog

    @SuppressLint("SetTextI18n")
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
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                if (mDay < 10){
                    dayS = "0$mDay"
                    monthS = if (mMonth < 10){
                        "0${mMonth + 1}"
                    } else{
                        "${mMonth + 1}"
                    }
                } else{
                    dayS = "$mDay"
                    monthS = if (mMonth < 10){
                        "0${mMonth + 1}"
                    } else{
                        "${mMonth + 1}"
                    }
                }
                yearS = "$mYear"

                date_textView.text = "$dayS/$monthS/$yearS"

            }, year, month, day)
            dpd.datePicker.minDate = c.timeInMillis + 86400000
            dpd.datePicker.maxDate = c.timeInMillis + 601200000//518400000

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

        order_summary_button.setOnClickListener{
            if (dayS.equals("") && monthS.equals("") && yearS.equals(""))
            {
                Toast.makeText(applicationContext, "Wybierz datę", Toast.LENGTH_LONG).show()

            }else{
                apiService.createOrder(
                    RequestOrder(
                        accessToken,
                        dayS,
                        monthS,
                        yearS
                    )
                )
                    .enqueue(object : Callback<ResponseOrder>{
                        override fun onFailure(call: Call<ResponseOrder>, t: Throwable) {
                            Toast.makeText(applicationContext, "Wystąpił błąd spróbuj ponownie później", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<ResponseOrder>, response: Response<ResponseOrder>) {
                            //Log.e("Number", response.body()!!.id_status)
                            CartActivity.fa!!.finish()
                            //Gdy produkty są wybrane z listy zamówień i okna wiszą w tle odpowiedno zamyka okna i otwiera żeby informacje były na bierząco
                            if(HistoryDetail.pa != null){
                                HistoryDetail.flag = true
                                HistoryOrderActivity.pa!!.finish()
                            }
                            Log.e("flaga", HistoryDetail.flag.toString())
                            val productsList = response.body()!!.products // Lista składników
                            orderPopup(response.body()!!.order_number, response.body()!!.date, productsList, response.body()!!.full_price)
                        }

                    })
            }

        }
    }

    var flag = false
    @SuppressLint("SetTextI18n", "InflateParams")
    fun orderPopup(orderId: String, orderDate: String, orderList: List<ResponseCartDetail>, orderPrice: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.order_info_popup, null)
        val orderIdText  = dialogLayout.findViewById<TextView>(R.id.OrderId)
        val orderDateText = dialogLayout.findViewById<TextView>(R.id.OrderDate)
        val orderListText = dialogLayout.findViewById<TextView>(R.id.OrderList)
        val orderPriceText = dialogLayout.findViewById<TextView>(R.id.OrderPrice)
        val buttonClose = dialogLayout.findViewById<Button>(R.id.OkButton)

        buttonClose.setOnClickListener{
            orderDialog.cancel()
            finish()
        }
        val range = orderList.size
        var productList = "Produkty:\n"
        for(i  in 0 until range){
            productList += " - " + orderList[i].name + " x" + orderList[i].amount
            if (i == range - 1)
            {}
            else productList += "\n"
        }
        orderListText.text = productList
        orderIdText.text = "Numer zamówienia: $orderId"
        orderDateText.text = "Data: $orderDate"
        orderPriceText.text = "Wartość: $orderPrice zł"
        builder.setView(dialogLayout)
        orderDialog = builder.create()
        orderDialog.show()
        orderDialog.setCanceledOnTouchOutside(false)
        orderDialog.setCancelable(false)
        flag = true
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
                        fetchProfile(token.token)
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
            SummaryListItem(it)
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
        summaryRecyclerView.isClickable = false

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
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseCart<List<ResponseCartDetail>>>,
                    response: Response<ResponseCart<List<ResponseCartDetail>>>
                ) {
                    handleFetchCartSuccess(response.body()!!.cart)
                    textView21.text = "%.2f zł".format(response.body()!!.full_price)
                    textView12.text = response.body()!!.seller_name
                    initializeRecyclerView()
                }
            })
    }

    private fun fetchProfile(token : String){
        apiService.fetchProfile(RequestToken(token))
            .enqueue(object : Callback<ResponseProfile>{
                override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
                    Toast.makeText(applicationContext, "Wystąpił błąd.\nSpróbuj ponownie później", Toast.LENGTH_LONG).show()
                }
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ResponseProfile>, response: Response<ResponseProfile>) {
                    textView14.text =
                        "${response.body()!!.name}\n${response.body()!!.street} ${response.body()!!.house_number}"
                }
            })
    }
}
