package com.swapi.swapikotlin.view.list

import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.api.Cart
import com.swapi.swapikotlin.api.model.CartDto
import kotlinx.android.synthetic.main.activity_cart.view.*
import java.util.*



class CartListItem(model: CartDto) : ModelAbstractItem<CartDto, CartListItem, CartListItem.CartListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.cart_type_id
    }

    override fun getViewHolder(v: View): CartListItemViewHolder {
        return CartListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_in_cart
    }

    override fun getIdentifier(): Long {
        return Objects.hash(model).toLong()
    }

    class CartListItemViewHolder(itemView: View) : FastAdapter.ViewHolder<CartListItem>(itemView) {

        private val titleText: TextView = itemView.findViewById(R.id.title)
        private val button: Button = itemView.findViewById(R.id.button2)
        private val countButton: ElegantNumberButton = itemView.findViewById(R.id.number_button)
        override fun bindView(item: CartListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model
            // Update view.
            titleText.text = model.title
            countButton.number = model.quantity.toString()
            button.setOnClickListener{

                //itemView.recyclerView.adapter.notifyDataSetChanged()
                //val viewHolder: RecyclerView.ViewHolder = item.getViewHolder(itemView)
                val position = adapterPosition

                Log.i("POZYCJA: ", "$position")
                Cart.deleteItem(position)

                //payloads.removeAt(position)

                Snackbar.make(itemView, R.string.cartDelete, Snackbar.LENGTH_SHORT).show()
            }
//            countButton.setOnClickListener {
//
//            }
        }

        override fun unbindView(item: CartListItem) {
            titleText.text = null
        }
    }
}