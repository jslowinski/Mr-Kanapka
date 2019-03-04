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
import com.mrkanapka.mrkanapkakotlin.view.SandwichFragment
import com.mrkanapka.mrkanapkakotlin.view.SaladFragment
import com.mrkanapka.mrkanapkakotlin.view.JuiceFragment

class TabFragment : Fragment(){

    var category = ArrayList<CategoryDto>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        category = arguments!!.getParcelableArrayList<CategoryDto>("category")
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
            if(position == 0)
                return SandwichFragment.newInstance(category[position].id_category)
            if(position == 1)
                return SaladFragment.newInstance(category[position].id_category)
            if(position == 2)
                return JuiceFragment.newInstance(category[position].id_category)
            return null
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


        fun newInstance(category: ArrayList<CategoryDto>): TabFragment {
            val fragment = TabFragment()
            val args = Bundle()
            args.putParcelableArrayList("category", category)
            fragment.arguments = args
            return fragment
        }
    }
}