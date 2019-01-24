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
import com.mrkanapka.mrkanapkakotlin.view.SandwichFragment
import com.mrkanapka.mrkanapkakotlin.view.SaladFragment
import com.mrkanapka.mrkanapkakotlin.view.JuiceFragment

class TabFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val x =  inflater.inflate(R.layout.tab_layout,null)
        tabLayout = x.findViewById<View>(R.id.tabs) as TabLayout
        viewPager = x.findViewById<View>(R.id.viewpager) as ViewPager

        viewPager.adapter = MyAdapter(childFragmentManager)
        tabLayout.post { (tabLayout.setupWithViewPager(viewPager)) }


        // TO DZIADOWSTWO TRZEBA BYŁO DODAC ABY NIE ODSWIEZALO CIAGLE PIREWSZEGO FRAGMENTU W TABIE
        viewPager.offscreenPageLimit = 3

        return x
    }
    internal inner class MyAdapter (fm : FragmentManager) : FragmentPagerAdapter(fm){
        override fun getItem(position: Int): Fragment? {
            when(position){
                0 -> return SandwichFragment()
                1 -> return SaladFragment()
                2 -> return JuiceFragment()
            }
            return null
        }

        override fun getCount(): Int {
            return int_items
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> return "Kanapki"
                1 -> return "Sałatki"
                2 -> return "Soki"
            }
            return null
        }
    }

    companion object {
        lateinit var tabLayout : TabLayout
        lateinit var viewPager: ViewPager
        val int_items= 3
    }
}