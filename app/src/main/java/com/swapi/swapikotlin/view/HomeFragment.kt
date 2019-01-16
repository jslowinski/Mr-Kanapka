package com.swapi.swapikotlin.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.swapi.swapikotlin.FoodDetail
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.api.Cart
import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.Url
import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.database.AndroidDatabase
import com.swapi.swapikotlin.database.entity.FilmEntity
import com.swapi.swapikotlin.manager.FilmsManager
import com.swapi.swapikotlin.view.list.FilmListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import com.mikepenz.fastadapter.FastAdapter
import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.swapi.swapikotlin.api.model.CartDto
import kotlinx.android.synthetic.main.item_film.view.*


class HomeFragment  : Fragment() {

    //to swipe refresh
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    private val fastItemAdapter: FastItemAdapter<FilmListItem> = FastItemAdapter()
    //region Tag

    private val TAG = HomeFragment::class.java.simpleName

    //endregion

    //region API

    private val filmsManager by lazy {
        FilmsManager()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()
    //endregion


    override fun onResume() {
        super.onResume()
        //Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
    }

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

    private fun handleFetchFilmsSuccess(films: List<FilmEntity>) {

        // Log the fact.
        Log.i(TAG, "Successfully fetched films.")
        // Convert to list items.
        val items = films.map {
            FilmListItem(it)
        }

        // Display result.
        fastItemAdapter.setNewList(items)
        Snackbar.make(root1, R.string.fetchSuccess, Snackbar.LENGTH_SHORT).show()
        swipe_refresh_layout.isRefreshing = false
        textView5.visibility = View.GONE
        imageView4.visibility = View.GONE
    }

    private fun handleFetchFilmsError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching films.")
        Log.e(TAG, throwable.localizedMessage)

        Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
        swipe_refresh_layout.isRefreshing = false
        textView5.visibility = View.VISIBLE
        imageView4.visibility = View.VISIBLE
    }

    //endregion

    private fun initializeRecyclerView() {
        //TU TRZEBA BYŁO ZMIENIC THIS NA CONTEXT

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = fastItemAdapter


        fastItemAdapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }

        fastItemAdapter.withEventHook(object : ClickEventHook<FilmListItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                viewHolder.itemView.run { listOf(button) }

            override fun onClick(view: View?, position: Int, fastAdapter: FastAdapter<FilmListItem>?, item: FilmListItem?) {
                if (view != null && item != null) {
                    onListItemClicked(view, item)
                }
            }
        })


    }

    private fun onListItemClicked(view: View, item: FilmListItem){
        var bool : Boolean = true
        val model = item.model
        for (item in Cart.cartList)
        {
          if(item.title == model.title) {
            item.quantity++
            bool = false
          }
        }
        if(bool) {
          val item = CartDto(model.url, model.title, 1)
          Cart.setInfoItem(item)
        }
        Snackbar.make(root1, R.string.cartSuccess, Snackbar.LENGTH_SHORT).show()

    }

    private fun onItemClicked(item: FilmListItem): Boolean {

        // Retrieve model.
        val film = item.model

        val foodDetail = Intent(context, FoodDetail::class.java)
        foodDetail.putExtra("Url", film.url)
        //foodDetail.putExtra("Name",film.title)
        startActivity(foodDetail)
        Url.Detail_id = film.url
        println(Url.Detail_id)

       return true
    }

    //endregion

    //region Progress Bar

    private fun showProgress() {
        progressBar1.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressBar1.visibility = View.GONE
    }


    //TO CO BYŁO POTRZEBNE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun addAndFetchFilms(progressbar: Boolean)
    {
        filmsManager
            .getFilms()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::handleFetchFilmsSuccess,
                this::handleFetchFilmsError
            )
            .addTo(disposables)

        filmsManager
            .downloadFilms()
            .andThen(filmsManager.getFilms())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() }
            .doFinally { hideProgress() }
            .subscribe(
                this::handleFetchFilmsSuccess,
                this::handleFetchFilmsError
            )
            .addTo(disposables)

    }

    //TO NAJWAŻNIEJSZE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeRecyclerView()
        // Initialize the handler instance

        mHandler = Handler()
        // Set an on refresh listener for swipe refresh layout
        swipe_refresh_layout.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                addAndFetchFilms(false)
                // Hide swipe to refresh icon animation
                //swipe_refresh_layout.isRefreshing = false
                //chowane jest w handleFetchSucces/false
            }
            // Execute the task
            mHandler.post(mRunnable)
        }
        if(savedInstanceState == null)
            addAndFetchFilms(true)
    }
}