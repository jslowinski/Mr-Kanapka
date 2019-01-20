package com.swapi.swapikotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.swapi.swapikotlin.api.Cart
import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.model.CartDto
import com.swapi.swapikotlin.api.model.DetailDto
import com.swapi.swapikotlin.api.model.ResponseDetail

import kotlinx.android.synthetic.main.activity_food_detail.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result.response




class FoodDetail : AppCompatActivity() {

    var message = ""
    var name = ""
    var photo_url = ""
    var url = 0
    var price = ""
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
            actionBar.title = "Szczegóły"
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
                val item = CartDto(url, name, numberButton.number.toInt(), photo_url)
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

        val apiService = SwapiClient.createDetail("http://zespol9-server.herokuapp.com/api/details/")
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
                for(i  in 0 until range!!){
                    skladnik += "- " + test!!.get(i).name.toString()
                    if (i == range-1)
                    {}
                    else skladnik += "\n"
                }

                price = priceS.toString()
                name = title.toString()
                message = description + "\n\n" + skladnik
                photo_url = body?.photo_url.toString()

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

        })

    }
}
