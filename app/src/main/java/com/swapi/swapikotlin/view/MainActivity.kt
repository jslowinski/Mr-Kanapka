package com.swapi.swapikotlin.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.R.layout
import com.swapi.swapikotlin.api.SwapiClient
import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.view.list.FilmListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {

  //region Tag

  private val TAG = MainActivity::class.java.simpleName

  //endregion
    //tu byłem ja
  //region API

  private val swapiService by lazy {
    SwapiClient.create()
  }

  private val disposables: CompositeDisposable = CompositeDisposable()

  //endregion


  //region Lifecycle

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_main)

    // Initialize RecyclerView.
    initializeRecyclerView()
  }

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

    Snackbar.make(root, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
  }

  //endregion


  //region List

  private val adapter: FastItemAdapter<FilmListItem> = FastItemAdapter()

  private fun initializeRecyclerView() {
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.itemAnimator = DefaultItemAnimator()
    recyclerView.adapter = adapter

    adapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }
  }

  private fun onItemClicked(item: FilmListItem): Boolean {

    // Retrieve model.
    val film = item.model

    // Show crawl.
    AlertDialog.Builder(this)
        .setPositiveButton("OK", null)
        .setTitle(film.title)
        .setMessage(film.openingCrawl)
        .show()

    // Return true to indicate adapter that event was consumed.
    return true
  }

  //endregion

  //region Progress Bar

  private fun showProgress() {
    progressBar.visibility = View.VISIBLE
  }

  private fun hideProgress() {
    progressBar.visibility = View.GONE
  }

  //endregion
}
