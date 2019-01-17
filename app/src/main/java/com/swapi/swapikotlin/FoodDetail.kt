package com.swapi.swapikotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.swapi.swapikotlin.api.Cart
import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.Url
import com.swapi.swapikotlin.api.model.CartDto
import com.swapi.swapikotlin.api.model.DetailDto

import kotlinx.android.synthetic.main.activity_food_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FoodDetail : AppCompatActivity() {

    var message = ""
    var name = ""
    var url = 0
    //val foodDetailBar : AppBarLayout = findViewById(R.id.food_detail_bar)
    //val nestedScrollView : NestedScrollView = findViewById(R.id.nestedScrollView)





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_food_detail)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        val button : Button = findViewById(R.id.add_to_order_button)
        val numberButton : ElegantNumberButton = findViewById(R.id.number_button)

        food_detail_bar.visibility = View.INVISIBLE
        nestedScrollView.visibility = View.INVISIBLE
        progressBar2.visibility = View.VISIBLE

        url = intent.getIntExtra("intVariableName",0)
        
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar!!.title = "Szczegóły"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        callDetail()



        button.setOnClickListener{
            var bool : Boolean = true
            for (item in Cart.cartList)
            {
                if(item.title == name) {
                    item.quantity+=numberButton.number.toInt()
                    bool = false
                }
            }
            if(bool) {
                val item = CartDto(url, name, numberButton.number.toInt())
                Cart.setInfoItem(item)
            }
            Snackbar.make(root, R.string.cartSuccess, Snackbar.LENGTH_SHORT).show()
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }

    fun callDetail(){

        val apiService = SwapiClient.createDetail("https://swapi.co/api/films/")
        val call = apiService.fetchDetail(url.toString())
        call?.enqueue(object : Callback<DetailDto> {
            override fun onFailure(call: Call<DetailDto>, t: Throwable) {
                progressBar2.visibility = View.GONE
                Snackbar.make(root, "Błąd pobierania informacji o produkcie", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<DetailDto>, response: Response<DetailDto>) {
                val body = response.body()

                val title = body?.title
                name = title.toString()
                message = body?.openingCrawl.toString()

                val titleText: TextView = findViewById(R.id.food_name)
                titleText.text = name

                findViewById<TextView>(R.id.food_description).apply {
                    text = message
                }
                food_detail_bar.visibility = View.VISIBLE
                nestedScrollView.visibility = View.VISIBLE
            }

        })

    }
}
