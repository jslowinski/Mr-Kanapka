package com.swapi.swapikotlin.view

import android.os.Bundle
import android.support.design.widget.Snackbar

import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager

import android.support.v7.widget.RecyclerView
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.view.list.FilmListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment  : Fragment() {

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
    }

    private fun handleFetchFilmsError(throwable: Throwable) {

        // Log an error.
        Log.e(TAG, "An error occurred while fetching films.")
        Log.e(TAG, throwable.localizedMessage)

        Snackbar.make(root1, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
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


        //TU JAKAŚ MAGIĄ Z ALT+ENTER ALE DZIAŁA XD
        // Show crawl.
        this!!.context?.let {
            AlertDialog.Builder(it)
                .setPositiveButton("OK", null)
                .setTitle(film.title)
                .setMessage(film.openingCrawl)
                .show()
        }

        // Return true to indicate adapter that event was consumed.
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

        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        return rootView
    }


    //TO NAJWAŻNIEJSZE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeRecyclerView()
    }

}