package com.swapi.swapikotlin.view

import android.os.Bundle
import android.os.Handler
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
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.model.PlanetDto
import com.swapi.swapikotlin.view.list.PlanetListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_planet.*


class PlanetFragment  : Fragment() {

    //to swipe refresh
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    private val adapter: FastItemAdapter<PlanetListItem> = FastItemAdapter()
    //region Tag

    private val TAG = PlanetFragment::class.java.simpleName

    //endregion

    //region API
    private val swapiService by lazy {
        SwapiClient.create()
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    //endregion

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

    private fun handleFetchPlanetsSuccess(planets: List<PlanetDto>) {

        // Log the fact.
        Log.i(TAG, "Successfully fetched planets.")

        // Convert to list items.
        val items = planets.map {
            PlanetListItem(it)
        }

        // Display result.
        adapter.setNewList(items)
        Snackbar.make(root1, R.string.fetchSuccess, Snackbar.LENGTH_SHORT).show()
        swipe_refresh_layout.isRefreshing = false
    }

    private fun handleFetchPlanetsError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching planets.")
        Log.e(TAG, throwable.localizedMessage)

        Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
        swipe_refresh_layout.isRefreshing = false
    }

    //endregion

    fun addAndFetchPlanets(progressbar: Boolean)
    {
        if(progressbar)
        {
            disposables.add(
                swapiService
                    .fetchPlanets("planets/?page=1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { response -> response.results }
                    .doOnSubscribe { showProgress() }
                    .doFinally { hideProgress() }
                    .subscribe(
                        { result -> handleFetchPlanetsSuccess(result) },
                        { throwable -> handleFetchPlanetsError(throwable) }
                    )
            )
        }
        else
        {
            disposables.add(
                swapiService
                    .fetchPlanets("planets/?page=1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { response -> response.results }
                    .subscribe(
                        { result -> handleFetchPlanetsSuccess(result) },
                        { throwable -> handleFetchPlanetsError(throwable) }
                    )
            )
        }

    }

    private fun initializeRecyclerView() {
        //TU TRZEBA BYŁO ZMIENIC THIS NA CONTEXT
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

//        adapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }
    }

//    private fun onItemClicked(item: PlanetListItem): Boolean {
//
//        // Retrieve model.
//        val film = item.model
//
//
//        //TU JAKAŚ MAGIĄ Z ALT+ENTER ALE DZIAŁA XD
//        // Show crawl.
//        this!!.context?.let {
//            AlertDialog.Builder(it)
//                .setPositiveButton("OK", null)
//                .setTitle(film.name)
//                .setMessage(film.openingCrawl)
//                .show()
//        }
//
//        // Return true to indicate adapter that event was consumed.
//        return true
//    }

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

        return inflater.inflate(R.layout.fragment_planet, container, false)


    }


    //TO NAJWAŻNIEJSZE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeRecyclerView()
        mHandler = Handler()


        // Set an on refresh listener for swipe refresh layout
        swipe_refresh_layout.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {

                addAndFetchPlanets(false)
                // Hide swipe to refresh icon animation
                //swipe_refresh_layout.isRefreshing = false
                //chowane jest w handleFetchSucces/false
            }

            // Execute the task
            mHandler.post(mRunnable)
        }
        addAndFetchPlanets(true)
    }

}