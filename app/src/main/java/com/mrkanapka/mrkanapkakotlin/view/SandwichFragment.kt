package com.mrkanapka.mrkanapkakotlin.view

import android.content.Intent
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
import com.mrkanapka.mrkanapkakotlin.api.Cart
import com.mrkanapka.mrkanapkakotlin.api.Url
import com.mrkanapka.mrkanapkakotlin.database.entity.ProductEntity
import com.mrkanapka.mrkanapkakotlin.manager.ProductsManager
import com.mrkanapka.mrkanapkakotlin.view.list.ProductListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_sandwich.*
import com.mikepenz.fastadapter.FastAdapter
import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mrkanapka.mrkanapkakotlin.api.model.CartDto
import com.mrkanapka.mrkanapkakotlin.database.entity.SaladEntity
import com.mrkanapka.mrkanapkakotlin.view.list.SaladListItem
import kotlinx.android.synthetic.main.item_menu.view.*


class SandwichFragment  : Fragment() {
    //unifikacja
    companion object {
        fun newInstance(category: Int): SandwichFragment {
            val fragment = SandwichFragment()
            val args = Bundle()
            args.putInt("category", category)
            fragment.arguments = args
            return fragment
        }
    }
    private var category = 0
    //to swipe refresh
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    private val fastItemAdapter: FastItemAdapter<ProductListItem> = FastItemAdapter()
    private val fastItemAdapter1: FastItemAdapter<SaladListItem> = FastItemAdapter()
    //region Tag

    private val TAG = SandwichFragment::class.java.simpleName

    //endregion
    private var cacheSucces : Boolean = false
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

        textView5.visibility = View.GONE
        imageView4.visibility = View.GONE
        // Log the fact.
        Log.i(TAG, "Successfully fetched Sandwichs.")
        // Convert to list items.
        val items = products.map {
            ProductListItem(it)
        }

        // Display result.
        fastItemAdapter.setNewList(items)
        Snackbar.make(root1, R.string.fetchSuccess, Snackbar.LENGTH_SHORT).show()

    }

    private fun handleFetchSandwichsCacheSuccess(products: List<ProductEntity>) {

        // Log the fact.
        Log.i(TAG, "Successfully fetched Sandwichs.")
        // Convert to list items.
        val items = products.map {
            ProductListItem(it)
        }
        // Display result.
        fastItemAdapter.setNewList(items)
        if(items.isEmpty()) {
            textView6.visibility = View.VISIBLE
            imageView5.visibility = View.VISIBLE
        }
        else {
            //Snackbar.make(root1, R.string.fetchSuccess, Snackbar.LENGTH_SHORT).show()
            swipe_refresh_layout.isRefreshing = false
            cacheSucces = true
        }

    }

    private fun handleFetchSandwichsError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching Sandwichs.")
        Log.e(TAG, throwable.localizedMessage)
        swipe_refresh_layout.isRefreshing = false
        //zaslepka internet z pobraniem z bazy
        if(cacheSucces) {
            Snackbar.make(root1, "Brak połączenia z internetem, tryb offline", Snackbar.LENGTH_SHORT).show()
        }
        else {
            textView5.visibility = View.VISIBLE
            imageView4.visibility = View.VISIBLE
        }


    }

    private fun handleFetchSandwichsCacheError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching Sandwichs.")
        Log.e(TAG, throwable.localizedMessage)

        Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
        swipe_refresh_layout.isRefreshing = false
        textView5.visibility = View.VISIBLE
        imageView4.visibility = View.VISIBLE
        cacheSucces = false
    }

    //endregion

    private fun handleFetchSaladsSuccess(products: List<SaladEntity>) {

        // Log the fact.
        Log.i(TAG, "Successfully fetched films.")
        // Convert to list items.
        val items = products.map {
            SaladListItem(it)
        }

        // Display result.
        fastItemAdapter1.setNewList(items)
        Snackbar.make(root1, R.string.fetchSuccess, Snackbar.LENGTH_SHORT).show()


    }

    private fun handleFetchSaladsCacheSuccess(products: List<SaladEntity>) {


        // Log the fact.
        Log.i(TAG, "Successfully fetched films.")
        // Convert to list items.
        val items = products.map {
            SaladListItem(it)
        }
        // Display result.
        fastItemAdapter1.setNewList(items)

        if(items.isEmpty()) {
            textView6.visibility = View.VISIBLE
            imageView5.visibility = View.VISIBLE
        }
        else {
            //Snackbar.make(root1, R.string.fetchSuccess, Snackbar.LENGTH_SHORT).show()
            swipe_refresh_layout.isRefreshing = false
            cacheSucces = true
        }

    }

    private fun handleFetchSaladsError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching films.")
        Log.e(TAG, throwable.localizedMessage)
        swipe_refresh_layout.isRefreshing = false
        //zaslepka internet z pobraniem z bazy



        if(cacheSucces) {
            Snackbar.make(root1, "Brak połączenia z internetem, tryb offline", Snackbar.LENGTH_SHORT).show()
        }
        else {
            textView5.visibility = View.VISIBLE
            imageView4.visibility = View.VISIBLE
        }

    }

    private fun handleFetchSaladsCacheError(throwable: Throwable) {


        // Log an error.
        Log.e(TAG, "An error occurred while fetching salads.")
        Log.e(TAG, throwable.localizedMessage)



        Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
        swipe_refresh_layout.isRefreshing = false
        textView5.visibility = View.VISIBLE
        imageView4.visibility = View.VISIBLE
        cacheSucces = false
    }
    //endregion

    private fun initializeRecyclerView() {
        //TU TRZEBA BYŁO ZMIENIC THIS NA CONTEXT

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()

        if(category == 0) {
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
        if(category == 1) {
            recyclerView.adapter = fastItemAdapter1
            fastItemAdapter1.withOnClickListener { _, _, item, _ -> onItemClicked(item) }
            fastItemAdapter1.withEventHook(object : ClickEventHook<SaladListItem>() {
                override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                    viewHolder.itemView.run { listOf(button) }
                override fun onClick(view: View?, position: Int, fastAdapter: FastAdapter<SaladListItem>?, item: SaladListItem?) {
                    if (view != null && item != null) {
                        onListItemClicked(view, item)
                    }
                }
            })
        }




    }

    private fun onListItemClicked(view: View, item: ProductListItem){
        var bool : Boolean = true
        val model = item.model
        for (item in Cart.cartList)
        {
          if(item.title == model.name) {
            item.quantity++
            bool = false
          }
        }
        if(bool) {
          val item = CartDto(model.id_product, model.name, model.price, 1, model.id_product,  model.photo_url)
          Cart.setInfoItem(item)
        }
        Snackbar.make(root1, R.string.cartSuccess, Snackbar.LENGTH_SHORT).show()

    }

    private fun onItemClicked(item: SaladListItem): Boolean {

        // Retrieve model.
        val product = item.model

        val foodDetail = Intent(context, FoodDetail::class.java)
        foodDetail.putExtra("intVariableName", product.id_product)
        //foodDetail.putExtra("Name",film.title)
        startActivity(foodDetail)
        //Url.Detail_id = product.id_product


       return true
    }

    private fun onListItemClicked(view: View, item: SaladListItem){
        var bool : Boolean = true
        val model = item.model
        for (item in Cart.cartList)
        {
            if(item.title == model.name) {
                item.quantity++
                bool = false
            }
        }
        if(bool) {
            val item = CartDto(model.id_product, model.name, model.price, 1, model.id_product,  model.photo_url)
            Cart.setInfoItem(item)
        }
        Snackbar.make(root1, R.string.cartSuccess, Snackbar.LENGTH_SHORT).show()

    }

    private fun onItemClicked(item: ProductListItem): Boolean {

        // Retrieve model.
        val product = item.model

        val foodDetail = Intent(context, FoodDetail::class.java)
        foodDetail.putExtra("intVariableName", product.id_product)
        //foodDetail.putExtra("Name",film.title)
        startActivity(foodDetail)
        //Url.Detail_id = product.id_product


        return true
    }
    //endregion

    //region Progress Bar

    private fun showProgress() {
        swipe_refresh_layout.isRefreshing = true
    }

    private fun hideProgress() {
        swipe_refresh_layout.isRefreshing = false
    }


    //TO CO BYŁO POTRZEBNE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_sandwich, container, false)
    }

    private fun addAndFetchSandwiches(progressbar: Boolean)
    {
        textView5.visibility = View.GONE
        imageView4.visibility = View.GONE
        textView6.visibility = View.GONE
        imageView5.visibility = View.GONE
        //From cache
        productsManager
            .getSandwiches()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleFetchSandwichsCacheSuccess,
                this::handleFetchSandwichsCacheError
            )
            .addTo(disposables)

        //From api
        productsManager
            .downloadSandwiches()
            .andThen(productsManager.getSandwiches())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() }
            .doFinally { hideProgress() }
            .subscribe(
                this::handleFetchSandwichsSuccess,
                this::handleFetchSandwichsError
            )
            .addTo(disposables)

    }

    private fun addAndFetchSalads(progressbar: Boolean)
    {
        textView5.visibility = View.GONE
        imageView4.visibility = View.GONE
        textView6.visibility = View.GONE
        imageView5.visibility = View.GONE
        //From cache
        productsManager
            .getSalads()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleFetchSaladsCacheSuccess,
                this::handleFetchSaladsCacheError
            )
            .addTo(disposables)

        //From api
        productsManager
            .downloadSalads()
            .andThen(productsManager.getSalads())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() }
            .doFinally { hideProgress() }
            .subscribe(
                this::handleFetchSaladsSuccess,
                this::handleFetchSaladsError
            )
            .addTo(disposables)

    }

    //TO NAJWAŻNIEJSZE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getInt("category").let { category = it!! }
        initializeRecyclerView()
        // Initialize the handler instance

        mHandler = Handler()
        // Set an on refresh listener for swipe refresh layout
        swipe_refresh_layout.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                if(category == 0)
                    addAndFetchSandwiches(false)
                if(category == 1)
                    addAndFetchSalads(false)
                // Hide swipe to refresh icon animation
                //swipe_refresh_layout.isRefreshing = false
                //chowane jest w handleFetchSucces/false
            }
            // Execute the task
            mHandler.post(mRunnable)
        }
        if(savedInstanceState == null) {
            if(category == 0)
                addAndFetchSandwiches(true)
            if(category == 1)
                addAndFetchSalads(true)
        }

    }
}