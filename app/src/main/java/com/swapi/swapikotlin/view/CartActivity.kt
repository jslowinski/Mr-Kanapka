package com.swapi.swapikotlin.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.swapi.swapikotlin.FoodDetail
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.api.Cart
import com.swapi.swapikotlin.view.list.CartListItem
import kotlinx.android.synthetic.main.activity_cart.*
import java.util.*
import java.util.Collections.swap



class CartActivity : AppCompatActivity() {

    private val adapter: FastItemAdapter<CartListItem> = FastItemAdapter()

    private val items = Cart.infoItem().map { CartListItem(it) }.toMutableList()

    private var ile_deletow = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val actionBar = supportActionBar
        actionBar!!.title = "Koszyk"
        actionBar.setDisplayHomeAsUpEnabled(true)

        initializeRecyclerView()
    }



    private fun initializeRecyclerView() {

        // Convert to list items.
        //items = Cart.infoItem().map { CartListItem(it) }.toMutableList()

        if (items.size != 0){
            textView3.visibility = View.GONE
            imageView2.visibility = View.GONE
        }
        else {
            textView3.visibility = View.VISIBLE
            imageView2.visibility = View.VISIBLE
        }


        adapter.setNewList(items)
        //TU TRZEBA BYŁO ZMIENIC THIS NA CONTEXT
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.itemAnimator = DefaultItemAnimator()
        cartRecyclerView.adapter = adapter

        adapter.withOnClickListener { _, _, item, _ -> onItemClicked(item) }
        setRecyclerViewItemTouchListener()
    }

    private fun setRecyclerViewItemTouchListener() {

        //1
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                //2
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //3
                val position = viewHolder.adapterPosition
                Log.i("USUWANIE Z KOSZYKA: ", "$position")

                Cart.deleteItem(position)
                items.removeAt(position)

                //adapter.notifyItemRemoved(position)
                //adapter.notifyItemRangeChanged(position, items.size)
//                for(i in (position+1)..(items.size-1))
//                    adapter.set(i-1, items[i])


                //adapter.setNewList(items)
                //recyclerView.adapter!!.notifyItemRemoved(position)
                //recyclerView.adapter!!.notifyItemChanged(position, items)

                //recyclerView.adapter!!.notifyDataSetChanged()
                initializeRecyclerView()
                Snackbar.make(root1, R.string.cartDelete, Snackbar.LENGTH_SHORT).show()
            }
        }

        //4
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(cartRecyclerView)
    }

    private fun onItemClicked(item: CartListItem): Boolean {

        // Retrieve model.
        val itemCart = item.model

        val foodDetail = Intent(this, FoodDetail::class.java)
        //foodDetail.putExtra("OpeningCrawl", "z koszyka")
        //foodDetail.putExtra("Name", itemCart.title)
        foodDetail.putExtra("Url", itemCart.url)
        startActivity(foodDetail)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }
}
