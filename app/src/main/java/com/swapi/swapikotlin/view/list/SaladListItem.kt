package com.swapi.swapikotlin.view.list

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter.ViewHolder
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.database.entity.SaladEntity
import com.swapi.swapikotlin.view.list.SaladListItem.SaladListItemViewHolder
import java.util.Objects.hash

class SaladListItem(model: SaladEntity) : ModelAbstractItem<SaladEntity, SaladListItem, SaladListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.planet_type_id
    }

    override fun getViewHolder(v: View): SaladListItemViewHolder {
        return SaladListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_planets
    }

    override fun getIdentifier(): Long {
        return hash(model).toLong()
    }

    class SaladListItemViewHolder(itemView: View) : ViewHolder<SaladListItem>(itemView) {

        private val titleText: TextView = itemView.findViewById(R.id.title)
        private val directorText: TextView = itemView.findViewById(R.id.director)
        private val producerText: TextView = itemView.findViewById(R.id.producer)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val button: Button = itemView.findViewById(R.id.button)

        override fun bindView(item: SaladListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model

            // Update view.
            titleText.text = model.name
            directorText.text = model.description
            producerText.text = model.price + " zł"
            Glide.with(itemView).load(model.photo_url).into(imageView)

        }

        override fun unbindView(item: SaladListItem) {
            titleText.text = null
            directorText.text = null
            producerText.text = null
        }
    }
}