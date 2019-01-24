package com.mrkanapka.mrkanapkakotlin.view.list

import android.view.View
import android.widget.TextView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.model.CartDto
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
        //private val button: Button = itemView.findViewById(R.id.button2)
        private val countButton: ElegantNumberButton = itemView.findViewById(R.id.number_button)
        override fun bindView(item: CartListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model
            // Update view.
            titleText.text = model.title
            countButton.number = model.quantity.toString()

        }

        override fun unbindView(item: CartListItem) {
            titleText.text = null
        }
    }
}