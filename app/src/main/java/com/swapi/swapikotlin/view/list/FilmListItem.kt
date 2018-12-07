package com.swapi.swapikotlin.view.list

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter.ViewHolder
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.view.list.FilmListItem.FilmListItemViewHolder
import java.util.Objects.hash

class FilmListItem(model: FilmDto) : ModelAbstractItem<FilmDto, FilmListItem, FilmListItemViewHolder>(model) {

  override fun getType(): Int {
    return R.id.film_type_id
  }

  override fun getViewHolder(v: View): FilmListItemViewHolder {
    return FilmListItemViewHolder(v)
  }

  override fun getLayoutRes(): Int {
    return R.layout.item_film
  }

  override fun getIdentifier(): Long {
    return hash(model).toLong()
  }

  class FilmListItemViewHolder(itemView: View) : ViewHolder<FilmListItem>(itemView) {

    private val titleText: TextView = itemView.findViewById(R.id.title)
    private val directorText: TextView = itemView.findViewById(R.id.director)
    private val producerText: TextView = itemView.findViewById(R.id.producer)

    override fun bindView(item: FilmListItem, payloads: MutableList<Any>) {

      // Retrieve model.
      val model = item.model

      // Update view.
      titleText.text = model.title
      directorText.text = model.director
      producerText.text = model.producer
    }

    override fun unbindView(item: FilmListItem) {
      titleText.text = null
      directorText.text = null
      producerText.text = null
    }

  }
}