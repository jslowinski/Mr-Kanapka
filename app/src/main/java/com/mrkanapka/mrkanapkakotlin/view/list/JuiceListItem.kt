package com.mrkanapka.mrkanapkakotlin.view.list

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter.ViewHolder
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.database.entity.JuiceEntity
import java.util.Objects.hash

class JuiceListItem(model: JuiceEntity) : ModelAbstractItem<JuiceEntity, JuiceListItem, JuiceListItem.JuiceListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.juice_type_id
    }

    override fun getViewHolder(v: View): JuiceListItemViewHolder {
        return JuiceListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_menu
    }

    override fun getIdentifier(): Long {
        return hash(model).toLong()
    }

    class JuiceListItemViewHolder(itemView: View) : ViewHolder<JuiceListItem>(itemView) {

        private val nameText: TextView = itemView.findViewById(R.id.name)
        private val descriptionText: TextView = itemView.findViewById(R.id.menu_description)
        private val priceText: TextView = itemView.findViewById(R.id.menu_price)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val button: Button = itemView.findViewById(R.id.button)

        override fun bindView(item: JuiceListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model

            // Update view.
            nameText.text = model.name
            descriptionText.text = model.description
            priceText.text = model.price + " zł"
            Glide.with(itemView).load(model.photo_url).into(imageView)

        }

        override fun unbindView(item: JuiceListItem) {
            nameText.text = null
            descriptionText.text = null
            priceText.text = null
        }
    }
}