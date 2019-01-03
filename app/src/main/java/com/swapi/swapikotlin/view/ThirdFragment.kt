package com.swapi.swapikotlin.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.swapi.swapikotlin.FoodDetail
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.view.list.FilmListItem


class ThirdFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_third, container, false)

    }

}