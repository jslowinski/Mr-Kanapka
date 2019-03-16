package com.mrkanapka.mrkanapkakotlin.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mrkanapka.mrkanapkakotlin.FoodDetail
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.Cart
import com.mrkanapka.mrkanapkakotlin.api.model.*
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity
import com.mrkanapka.mrkanapkakotlin.manager.TokenManager
import com.mrkanapka.mrkanapkakotlin.view.list.CartListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.item_in_cart.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CartActivity : AppCompatActivity() {

    private val adapter: FastItemAdapter<CartListItem> = FastItemAdapter()

    //private val items = Cart.infoItem().map { CartListItem(it) }.toMutableList()

    private val apiService by lazy {
        ApiClient.create()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    private val tokenManager by lazy {
        TokenManager()
    }

    private var accessToken : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val actionBar = supportActionBar
        actionBar!!.title = "Koszyk"
        actionBar.setDisplayHomeAsUpEnabled(true)


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
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.itemAnimator = DefaultItemAnimator()
        cartRecyclerView.adapter = adapter

        adapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }
        adapter.withEventHook(object : ClickEventHook<CartListItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                viewHolder.itemView.run { listOf(button2) }

            override fun onClick(view: View?, position: Int, fastAdapter: FastAdapter<CartListItem>?, item: CartListItem?) {
                apiService.deleteProductCart(RequestDeleteCart(accessToken,item!!.model.id_product))
                    .enqueue(object : Callback<ResponseDefault>{
                        override fun onFailure(call: Call<ResponseDefault>, t: Throwable) {
                            Toast.makeText(applicationContext, "Wystąpił błąd.\nSpróbuj ponownie później", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<ResponseDefault>, response: Response<ResponseDefault>) {
                            showCart(accessToken)
                        }
                    })
            }
        })
//        setRecyclerViewItemTouchListener()
    }

//    private fun onListItemClicked(view: View, item: CartListItem){
//
//        val position = adapter.getAdapterPosition(item)
//        Cart.deleteItem(position)
//        items.removeAt(position)
//        //adapter.notifyItemRemoved(position)
//        Snackbar.make(root1, R.string.cartDelete, Snackbar.LENGTH_SHORT).show()
//        initializeRecyclerView()
//
//    }

//    private fun setRecyclerViewItemTouchListener() {
//
//        //1
//        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
//            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
//                //2
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
//                //3
//                val position = viewHolder.adapterPosition
//                Log.i("USUWANIE Z KOSZYKA: ", "$position")
//
//                Cart.deleteItem(position)
//                items.removeAt(position)
//
//                //adapter.notifyItemRemoved(position)
//                //adapter.notifyItemRangeChanged(position, items.size)
////                for(i in (position+1)..(items.size-1))
////                    adapter.set(i-1, items[i])
//
//
//                //adapter.setNewList(items)
//                //recyclerView.adapter!!.notifyItemRemoved(position)
//                //recyclerView.adapter!!.notifyItemChanged(position, items)
//
//                //recyclerView.adapter!!.notifyDataSetChanged()
//                initializeRecyclerView()
//                Snackbar.make(root1, R.string.cartDelete, Snackbar.LENGTH_SHORT).show()
//            }
//        }
//
//        //4
//        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
//        itemTouchHelper.attachToRecyclerView(cartRecyclerView)
//    }

    private fun onItemClicked(item: CartListItem): Boolean {

        // Retrieve model.
        val itemCart = item.model

        val foodDetail = Intent(this, FoodDetail::class.java)
        //foodDetail.putExtra("OpeningCrawl", "z koszyka")
        //foodDetail.putExtra("Name", itemCart.title)
        foodDetail.putExtra("intVariableName", itemCart.id_product)
        foodDetail.putExtra("token", accessToken)
        //foodDetail.putExtra("Name",film.title)
        startActivity(foodDetail)
        finish()
        //Url.Detail_id = itemCart.id_product
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
                    if (response.body()!!.count == 0) {
                        imageView2.visibility = View.VISIBLE
                        textView3.visibility = View.VISIBLE
                    } else {
                        imageView2.visibility = View.GONE
                        textView3.visibility = View.GONE
                    }
                    handleFetchCartSuccess(response.body()!!.cart)
                    initializeRecyclerView()
                }
            })
    }
}
