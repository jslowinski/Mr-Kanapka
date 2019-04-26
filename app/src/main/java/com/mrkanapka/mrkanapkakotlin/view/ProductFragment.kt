package com.mrkanapka.mrkanapkakotlin.view

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mrkanapka.mrkanapkakotlin.FoodDetail
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.database.entity.ProductEntity
import com.mrkanapka.mrkanapkakotlin.manager.ProductsManager
import com.mrkanapka.mrkanapkakotlin.view.list.ProductListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_product.*
import com.mikepenz.fastadapter.FastAdapter
import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.Request.RequestAddCart
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseDefault
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.item_menu.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductFragment  : Fragment() {

    companion object {
        fun newInstance(category: Int, token: String, id_seller: Int): ProductFragment {
            val fragment = ProductFragment()
            val args = Bundle()
            args.putInt("category", category)
            args.putInt("seller", id_seller)
            args.putString("token", token)
            fragment.arguments = args
            return fragment
        }
    }
    private var token = ""
    private var id_seller: Int = 0
    private var category = 0
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    private val apiService by lazy {
        ApiClient.create()
    }

    private val fastItemAdapter: FastItemAdapter<ProductListItem> = FastItemAdapter()
    //region Tag

    private val TAG = ProductFragment::class.java.simpleName

    //endregion

    //region API

    private val productsManager by lazy {
        ProductsManager()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()
    //endregion

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

    private fun handleFetchSandwichsSuccess(products: List<ProductEntity>) {

        // Log the fact.
        Log.i(TAG, "Successfully fetched Sandwichs.")
        // Convert to list items.
        val items = products.map {
            ProductListItem(it)
        }
        // Display result.
        fastItemAdapter.setNewList(items)
        imageView4.visibility = View.GONE
        textView27.visibility = View.GONE
        Snackbar.make(root1, R.string.fetchSuccess, Snackbar.LENGTH_SHORT).show()

    }

    private fun handleFetchSandwichsCacheSuccess(products: List<ProductEntity>) {

        // Log the fact.
        Log.i(TAG, "CACHE: Successfully fetched Sandwichs.")
        // Convert to list items.
        val items = products.map {
            ProductListItem(it)
        }
        // Display result.
        fastItemAdapter.setNewList(items)
        imageView4.visibility = View.GONE
        textView27.visibility = View.GONE
    }

    private fun handleFetchSandwichsError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching Sandwichs.")
        Log.e(TAG, throwable.localizedMessage)
        Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
    }

    private fun handleFetchSandwichsCacheError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching Sandwichs.")
        Log.e(TAG, throwable.localizedMessage)
        Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()

    }

    //endregion

    private fun initializeRecyclerView() {
        //TU TRZEBA BYŁO ZMIENIC THIS NA CONTEXT

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = fastItemAdapter


        fastItemAdapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }

        fastItemAdapter.withEventHook(object : ClickEventHook<ProductListItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                viewHolder.itemView.run { listOf(button) }

            override fun onClick(view: View?, position: Int, fastAdapter: FastAdapter<ProductListItem>?, item: ProductListItem?) {
                if (view != null && item != null) {
                    onListItemClicked(view, item)
                }
            }
        })


    }

    private fun onListItemClicked(view: View, item: ProductListItem){
        println("sukces " + token + " " + item.model.id_product + " " + 1)
        Log.e("Token: ", token)
        Log.e("ID: ", item.model.id_product.toString())

        apiService.addCart(
            RequestAddCart(
                token,
                item.model.id_product,
                1
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

                        Snackbar.make(root1, R.string.cartSuccess, Snackbar.LENGTH_SHORT).show()
                    }
                    if (response.code() == 400) //Bad token
                    {
                        println("cos poszlo nie tak")
                        //Toast.makeText(applicationContext, "Zalogowano się z innego urządzenia\nZaloguj się ponownie", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun onItemClicked(item: ProductListItem): Boolean {

        // Retrieve model.
        val product = item.model

        val foodDetail = Intent(context, FoodDetail::class.java)
        foodDetail.putExtra("intVariableName", product.id_product)
        foodDetail.putExtra("token", token)
        foodDetail.putExtra("fromCart", 0)
        foodDetail.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //foodDetail.putExtra("Name",film.title)
        startActivity(foodDetail)
        //Url.Detail_id = product.id_product


       return true
    }

    //endregion

    //region Progress Bar

    private fun showProgress() {
        //swipe_refresh_layout.isRefreshing = true
    }

    private fun hideProgress() {
        //swipe_refresh_layout.isRefreshing = false
    }


    //TO CO BYŁO POTRZEBNE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    fun addAndFetchSandwiches(progressbar: Boolean)
    {
        //From cache
        productsManager
            .getProducts(category, id_seller)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleFetchSandwichsCacheSuccess,
                this::handleFetchSandwichsCacheError
            )
            .addTo(disposables)
        //From api
        if (progressbar) {
            productsManager
                .downloadProducts("products/" + category + "/seller/" + id_seller, category, id_seller)
                .andThen(productsManager.getProducts(category, id_seller))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doFinally { hideProgress() }
                .subscribe(
                    this::handleFetchSandwichsSuccess,
                    this::handleFetchSandwichsError
                )
                .addTo(disposables)
        }
        else {
            productsManager
                .downloadProducts("products/" + category + "/seller/" + id_seller, category, id_seller)
                .andThen(productsManager.getProducts(category, id_seller))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleFetchSandwichsSuccess,
                    this::handleFetchSandwichsError
                )
                .addTo(disposables)
        }

    }

    //TO NAJWAŻNIEJSZE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getInt("category").let { category = it!! }
        id_seller = arguments!!.getInt("seller")
        token = arguments?.getString("token").toString()
        initializeRecyclerView()
        // Initialize the handler instance
        addAndFetchSandwiches(false)
        mHandler = Handler()
        // Set an on refresh listener for swipe refresh layout
        swipe_refresh_layout1.setProgressBackgroundColorSchemeColor(0)
        swipe_refresh_layout1.setColorSchemeColors(0)
        swipe_refresh_layout1.setOnRefreshListener {
            // Initialize a new Runnable
            swipe_refresh_layout1.isRefreshing = false
            mRunnable = Runnable {


                // Hide swipe to refresh icon animation
                addAndFetchSandwiches(false)
                //swipe_refresh_layout.isRefreshing = false
                //chowane jest w handleFetchSucces/false
            }
            // Execute the task
            mHandler.post(mRunnable)
        }
    }
}