package com.mrkanapka.mrkanapkakotlin

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mrkanapka.mrkanapkakotlin.api.model.CategoryDto
import com.mrkanapka.mrkanapkakotlin.view.ProductFragment
import kotlinx.android.synthetic.main.app_bar_main2.*

class TabFragment : Fragment(){

    var category = ArrayList<CategoryDto>()
    var token = ""

    var id_seller: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        category = arguments!!.getParcelableArrayList<CategoryDto>("category")
        token = arguments!!.getString("token")
        id_seller = arguments!!.getInt("seller")
        for (item in category) println(item)
        val x =  inflater.inflate(R.layout.tab_layout,null)
        tabLayout = x.findViewById<View>(R.id.tabs) as TabLayout
        viewPager = x.findViewById<View>(R.id.viewpager) as ViewPager

        int_items = category.size
        viewPager.adapter = MyAdapter(childFragmentManager)
        tabLayout.post { (tabLayout.setupWithViewPager(viewPager)) }
        // TO DZIADOWSTWO TRZEBA BYŁO DODAC ABY NIE ODSWIEZALO CIAGLE PIREWSZEGO FRAGMENTU W TABIE
        viewPager.offscreenPageLimit = 9

        return x
    }
    internal inner class MyAdapter (fm : FragmentManager) : FragmentPagerAdapter(fm){

        override fun getItem(position: Int): Fragment? {
            return ProductFragment.newInstance(category[position].id_category, token, id_seller)
        }

        override fun getCount(): Int {
            return int_items
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return category[position].name
        }

    }

    companion object {
        lateinit var tabLayout : TabLayout
        lateinit var viewPager: ViewPager
        var int_items = 0


        fun newInstance(
            category: ArrayList<CategoryDto>,
            token: String,
            id_seller: Int
        ): TabFragment {
            val fragment = TabFragment()
            val args = Bundle()
            args.putParcelableArrayList("category", category)
            args.putString("token", token)
            args.putInt("seller", id_seller)
            fragment.arguments = args
            return fragment
        }
    }
}