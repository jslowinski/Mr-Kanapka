package com.swapi.swapikotlin.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.AlarmClock.EXTRA_MESSAGE
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
import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.view.list.FilmListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class HomeFragment  : Fragment() {

    //to swipe refresh
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    private val adapter: FastItemAdapter<FilmListItem> = FastItemAdapter()
    //region Tag

    private val TAG = HomeFragment::class.java.simpleName

    //endregion

    //region API

    private val swapiService by lazy {
        SwapiClient.create()
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

    private fun handleFetchFilmsSuccess(films: List<FilmDto>) {

        // Log the fact.
        Log.i(TAG, "Successfully fetched films.")

        // Convert to list items.
        val items = films.map {
            FilmListItem(it)
        }

        // Display result.
        adapter.setNewList(items)
        Snackbar.make(root1, R.string.fetchSuccess, Snackbar.LENGTH_SHORT).show()
        swipe_refresh_layout.isRefreshing = false
    }

    private fun handleFetchFilmsError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching films.")
        Log.e(TAG, throwable.localizedMessage)

        Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
        swipe_refresh_layout.isRefreshing = false
    }

    //endregion

    private fun initializeRecyclerView() {
        //TU TRZEBA BYŁO ZMIENIC THIS NA CONTEXT
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        adapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }


    }

    private fun onItemClicked(item: FilmListItem): Boolean {

        // Retrieve model.
        val film = item.model
//
//
//        //TU JAKAŚ MAGIĄ Z ALT+ENTER ALE DZIAŁA XD
//        // Show crawl.
//        this!!.context?.let {
//            AlertDialog.Builder(it)
//                .setPositiveButton("OK", null)
//                .setTitle(film.title)
//                .setMessage(film.openingCrawl)
//                .show()
//        }
//
//        // Return true to indicate adapter that event was consumed.

        val foodDetail = Intent(context, FoodDetail::class.java)
        foodDetail.putExtra(EXTRA_MESSAGE, film.openingCrawl)
        startActivity(foodDetail)

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

    fun addAndFetchFilms(progressbar: Boolean)
    {
        if(progressbar)
        {
            disposables.add(
                swapiService
                    .fetchFilms()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { response -> response.results }
                    .doOnSubscribe { showProgress() }
                    .doFinally { hideProgress() }
                    .subscribe(
                        { result -> handleFetchFilmsSuccess(result) },
                        { throwable -> handleFetchFilmsError(throwable) }
                    )
            )
        }
        else
        {
            disposables.add(
                swapiService
                    .fetchFilms()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { response -> response.results }
                    .subscribe(
                        { result -> handleFetchFilmsSuccess(result) },
                        { throwable -> handleFetchFilmsError(throwable) }
                    )
            )
        }

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

        addAndFetchFilms(true)

    }

}