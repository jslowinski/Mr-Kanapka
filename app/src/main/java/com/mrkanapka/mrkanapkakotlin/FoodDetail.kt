package com.mrkanapka.mrkanapkakotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.mrkanapka.mrkanapkakotlin.api.Cart
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.CartDto
import com.mrkanapka.mrkanapkakotlin.api.model.RequestAddCart
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseDetail
import com.mrkanapka.mrkanapkakotlin.view.CartActivity

import kotlinx.android.synthetic.main.activity_food_detail.*
import kotlinx.android.synthetic.main.fragment_product.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FoodDetail : AppCompatActivity() {

    var message = ""
    var name = ""
    var photo_url = ""
    var url = 0
    var price = ""
    var token = ""
    //val foodDetailBar : AppBarLayout = findViewById(R.id.food_detail_bar)
    //val nestedScrollView : NestedScrollView = findViewById(R.id.nestedScrollView)
    var flag = 0
    private val apiService by lazy {
        ApiClient.create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_food_detail)

        val button : Button = findViewById(R.id.add_to_order_button)
        val numberButton : ElegantNumberButton = findViewById(R.id.number_button)
        
        food_detail_bar.visibility = View.INVISIBLE
        nestedScrollView.visibility = View.INVISIBLE
        progressBar2.visibility = View.VISIBLE

        url = intent.getIntExtra("intVariableName",0)
        token = intent.getStringExtra("token")
        flag = intent.getIntExtra("fromCart", 0)
        Log.e("token", token)
        
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Szczegóły"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        callDetail()



        button.setOnClickListener{
//            var bool : Boolean = true
//            for (item in Cart.cartList)
//            {
//                if(item.title == name) {
//                    item.quantity+=numberButton.number.toInt()
//                    bool = false
//                }
//            }
//            if(bool) {
//                val item = CartDto(url, name, price, numberButton.number.toInt(), url, photo_url)
//                Cart.setInfoItem(item)
//            }
//            Snackbar.make(root, R.string.cartSuccess, Snackbar.LENGTH_SHORT).show()
            apiService.addCart(RequestAddCart(token, url, numberButton.number.toInt()))
                .enqueue(object : Callback<ResponseDefault> {
                    override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                        Log.e("Status: ", "Fail connection")
                        //Toast.makeText(applicationContext, "Brak internetu, tryb offline", Toast.LENGTH_LONG).show()
                        //dialog.cancel()
                    }

                    override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                        println("sukces")
                        Log.e("Kod", response.code().toString())
                        if (response.code() == 200) //Good token
                        {
                            println("sukces")

                            Snackbar.make(root, R.string.cartSuccess, Snackbar.LENGTH_SHORT).show()
                        }
                        if (response.code() == 400) //Bad token
                        {
                            println("cos poszlo nie tak")
                            //Toast.makeText(applicationContext, "Zalogowano się z innego urządzenia\nZaloguj się ponownie", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        if (flag == 1) {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            finish()
        }

    }

    fun callDetail(){

        val apiService = ApiClient.createDetail("http://zespol9-server.herokuapp.com/api/details/")
        val call = apiService.fetchDetail(url.toString())
        call.enqueue(object : Callback<ResponseDetail> {
            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                progressBar2.visibility = View.GONE
                Snackbar.make(root, "Błąd pobierania informacji o produkcie", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseDetail>, response: Response<ResponseDetail>) {
                val body = response.body()
                val title = body?.name
                val test = body?.component // Lista składników
                val description = body?.description
                val priceS = body?.price
                var range = test?.size

                var skladnik = ""
                //Odwoływanie się do składników
                if (range != null && title != null ) {
                    for(i  in 0 until range){
                        skladnik += "- " + test!!.get(i).name.toString()
                        if (i == range-1)
                        {}
                        else skladnik += "\n"
                    }
                    price = priceS.toString()
                    name = title.toString()
                    message = description + "\n\n" + skladnik
                    photo_url = body.photo_url.toString()

                    val titleText: TextView = findViewById(R.id.food_name)
                    titleText.text = name

                    val priceText: TextView = findViewById(R.id.food_price)
                    priceText.text = price

                    findViewById<TextView>(R.id.food_description).apply {
                        text = message
                    }

                    val imageView: ImageView = findViewById(R.id.img_food)
                    Glide.with(this@FoodDetail).load(photo_url).into(imageView)
                    food_detail_bar.visibility = View.VISIBLE
                    nestedScrollView.visibility = View.VISIBLE
                    }
                else {
                    progressBar2.visibility = View.GONE
                    Snackbar.make(root, "Błąd pobierania informacji o produkcie", Snackbar.LENGTH_SHORT).show()
                    return
                }
            }
        })
    }
}
