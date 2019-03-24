package com.mrkanapka.mrkanapkakotlin.view.list

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.model.CartDto
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseCartDetail
import java.util.*



class CartListItem(model: ResponseCartDetail) : ModelAbstractItem<ResponseCartDetail, CartListItem, CartListItem.CartListItemViewHolder>(model) {

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
        private val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        //private val button: Button = itemView.findViewById(R.id.button2)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val countButton: ElegantNumberButton = itemView.findViewById(R.id.number_button_cart)
        private val textViewPriceSingle: TextView = itemView.findViewById(R.id.textViewPrice2)
        @SuppressLint("SetTextI18n")
        override fun bindView(item: CartListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model
            // Update view.
            var price = model.price.toDouble()
            titleText.text = model.name
            countButton.number = model.amount.toString()
            textViewPrice.text = "%.2f".format(price) + " zł"
            Glide.with(itemView).load(model.photo_url).into(imageView)
            textViewPriceSingle.text = "%.2f zł".format(model.price_per_one.toDouble())

        }

        override fun unbindView(item: CartListItem) {
            titleText.text = null
        }
    }
}