package com.mrkanapka.mrkanapkakotlin.view.list

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter.ViewHolder
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.database.entity.SaladEntity
import com.mrkanapka.mrkanapkakotlin.view.list.SaladListItem.SaladListItemViewHolder
import java.util.Objects.hash

class SaladListItem(model: SaladEntity) : ModelAbstractItem<SaladEntity, SaladListItem, SaladListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.salad_type_id
    }

    override fun getViewHolder(v: View): SaladListItemViewHolder {
        return SaladListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_menu
    }

    override fun getIdentifier(): Long {
        return hash(model).toLong()
    }

    class SaladListItemViewHolder(itemView: View) : ViewHolder<SaladListItem>(itemView) {

        private val nameText: TextView = itemView.findViewById(R.id.name)
        private val descriptionText: TextView = itemView.findViewById(R.id.menu_description)
        private val priceText: TextView = itemView.findViewById(R.id.menu_price)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val button: Button = itemView.findViewById(R.id.button)

        override fun bindView(item: SaladListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model

            // Update view.
            nameText.text = model.name
            descriptionText.text = model.description
            priceText.text = model.price + " zł"
            Glide.with(itemView).load(model.photo_url).into(imageView)

        }

        override fun unbindView(item: SaladListItem) {
            nameText.text = null
            descriptionText.text = null
            priceText.text = null
        }
    }
}