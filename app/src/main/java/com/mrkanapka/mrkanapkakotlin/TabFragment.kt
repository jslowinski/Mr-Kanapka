package com.mrkanapka.mrkanapkakotlin

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mrkanapka.mrkanapkakotlin.api.ApiClient
import com.mrkanapka.mrkanapkakotlin.api.model.CategoryDto
import com.mrkanapka.mrkanapkakotlin.view.SandwichFragment
import com.mrkanapka.mrkanapkakotlin.view.SaladFragment
import com.mrkanapka.mrkanapkakotlin.view.JuiceFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TabFragment : Fragment(){


    private val apiService by lazy {
        ApiClient.create()
    }
    private val disposables: CompositeDisposable = CompositeDisposable()

    private fun handleFetchCategorySuccess(category: List<CategoryDto>) {

        // Log the fact.
        Log.i("tabfragment", "Successfully fetched categories.")

        // Convert to list items.
//        val items = films.sortedBy {
//            it.episodeId
//        }.map {
//            FilmListItem(it)
//        }

        int_items = category.size
        viewPager.adapter = MyAdapter(childFragmentManager, category)
        tabLayout.post { (tabLayout.setupWithViewPager(viewPager)) }

    }

    private fun handleFetchCategoryError(throwable: Throwable) {

        // Log an error.
        Log.e("tabfragment", "An error occurred while fetching categories.")
        Log.e("tabfragment", throwable.localizedMessage)

        //Snackbar.make(root, R.string.fetchError, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        disposables.add(
            apiService
                .fetchCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.category }
                .subscribe(
                    { handleFetchCategorySuccess(it) },
                    { handleFetchCategoryError(it) }
                )
        )
        val x =  inflater.inflate(R.layout.tab_layout,null)
        tabLayout = x.findViewById<View>(R.id.tabs) as TabLayout
        viewPager = x.findViewById<View>(R.id.viewpager) as ViewPager




        // TO DZIADOWSTWO TRZEBA BYŁO DODAC ABY NIE ODSWIEZALO CIAGLE PIREWSZEGO FRAGMENTU W TABIE
        viewPager.offscreenPageLimit = 9

        return x
    }
    internal inner class MyAdapter (fm : FragmentManager, category: List<CategoryDto>) : FragmentPagerAdapter(fm){

        val list = category
        override fun getItem(position: Int): Fragment? {

            for (item in list) {
                return SandwichFragment.newInstance(item.id_category)
            }
            return null
        }

        override fun getCount(): Int {
            return int_items
        }

        override fun getPageTitle(position: Int): CharSequence? {
            for (item in list) {
                return item.name
            }
            return null
        }

    }

    companion object {
        lateinit var tabLayout : TabLayout
        lateinit var viewPager: ViewPager
        var int_items = 0
    }
}