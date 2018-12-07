package com.swapi.swapikotlin

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.support.v7.widget.Toolbar


class Main2Activity : AppCompatActivity() {

    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mNavigationView: NavigationView
    lateinit var mFragmentManager: FragmentManager
    lateinit var mFragmentTransaction: FragmentTransaction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mDrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        mNavigationView = findViewById<View>(R.id.navView) as NavigationView


        mFragmentManager = supportFragmentManager
        mFragmentTransaction = mFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.containerView, TabFragment()).commit()

        mNavigationView.setNavigationItemSelectedListener {
            menuItem -> mDrawerLayout.closeDrawers()

            if(menuItem.itemId==R.id.nav_item_inbox){
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, TabFragment()).commit()
            }

            if(menuItem.itemId==R.id.nav_item_sent){
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, SentFragment()).commit()
            }

            if(menuItem.itemId==R.id.nav_item_draft){
                val ft = mFragmentManager.beginTransaction()
                ft.replace(R.id.containerView, DraftFragment()).commit()
            }

            false
        }

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        val mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name)

        mDrawerLayout.setDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
    }
}
