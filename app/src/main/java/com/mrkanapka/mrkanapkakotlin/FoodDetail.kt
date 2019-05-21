package com.mrkanapka.mrkanapkakotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestAddCart
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDefault
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDetail
import com.mrkanapka.mrkanapkakotlin.view.CartActivity

import kotlinx.android.synthetic.main.activity_food_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FoodDetail : AppCompatActivity() {

    var message = ""
    var name = "Szczegóły"
    var photoUrl = ""
    private var url = 0
    var price = ""
    var token = ""
    //val foodDetailBar : AppBarLayout = findViewById(R.id.food_detail_bar)
    //val nestedScrollView : NestedScrollView = findViewById(R.id.nestedScrollView)
    private var flag = 0
    private val apiService by lazy {
        ApiClient.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_food_detail)

        val button : Button = findViewById(R.id.add_to_order_button)
        val numberButton : ElegantNumberButton = findViewById(R.id.number_button)
        numberButton.setRange(0,99)

        food_detail_bar.visibility = View.GONE
        nestedScrollView.visibility = View.GONE
        progressBar2.visibility = View.VISIBLE

        url = intent.getIntExtra("intVariableName",0)
        token = intent.getStringExtra("token")
        flag = intent.getIntExtra("fromCart", 0)
        Log.e("token", token)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = name
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        callDetail()



        button.setOnClickListener{
            apiService.addCart(
                RequestAddCart(
                    token,
                    url,
                    numberButton.number.toInt()
                )
            )
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
                finish()
                return super.onOptionsItemSelected(item)}
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
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

    private fun callDetail(){

        val apiService = ApiClient.createDetail("http://zespol9-server.herokuapp.com/api/details/")
        val call = apiService.fetchDetail(url.toString())
        call.enqueue(object : Callback<ResponseDetail> {
            override fun onFailure(call: Call<ResponseDetail>, t: Throwable) {
                progressBar2.visibility = View.GONE
                Snackbar.make(root, "Błąd pobierania informacji o produkcie", Snackbar.LENGTH_SHORT).show()
                imageView6.visibility = View.VISIBLE
                textView29.visibility = View.VISIBLE
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseDetail>, response: Response<ResponseDetail>) {
                val body = response.body()
                val title = body?.name
                val test = body?.component // Lista składników
                val description = body?.description
                val priceS = body?.price
                val range = test?.size

                var skladnik = ""
                //region Odwoływanie się do składników
                if (range != null && title != null ) {
                    if (range != 0)
                    {
                        skladnik = "\n\nSkładniki:\n"
                    }
                    for(i  in 0 until range){
                        skladnik += "- " + test[i].name
                        if (i == range-1)
                        {}
                        else skladnik += "\n"
                    }
                    //endregion
                    price = priceS.toString()
                    name = title.toString()
                    message = description + skladnik
                    photoUrl = body.photo_url.toString()

                    val actionBar = supportActionBar
                    if (actionBar != null) {
                        actionBar.title = name
                        actionBar.setDisplayHomeAsUpEnabled(true)
                    }
                    food_name.text = name

                    food_price.text = "$price zł"

                    food_description.text = message

                    //region Ukrywanie zdjęcia jak go nie ma
                    Log.e("Url: ", photoUrl)
                    val imageView: ImageView = findViewById(R.id.img_food)
                    if (photoUrl.equals("https://res.cloudinary.com/daaothls9/"))
                    {
                        nestedScrollView.visibility = View.VISIBLE
                    }
                    else{
                        Glide.with(this@FoodDetail).load(photoUrl).into(imageView)
                        food_detail_bar.visibility = View.VISIBLE
                        nestedScrollView.visibility = View.VISIBLE
                    }
                    //endregion
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
