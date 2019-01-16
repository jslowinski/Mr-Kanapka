package com.swapi.swapikotlin.view.list

import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter.ViewHolder
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.api.Cart
import com.swapi.swapikotlin.api.model.CartDto
import com.swapi.swapikotlin.api.model.FilmDto
import com.swapi.swapikotlin.database.entity.FilmEntity
import com.swapi.swapikotlin.view.list.FilmListItem.FilmListItemViewHolder
import java.util.Objects.hash

class FilmListItem(model: FilmEntity) : ModelAbstractItem<FilmEntity, FilmListItem, FilmListItemViewHolder>(model) {

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
    private val button: Button = itemView.findViewById(R.id.button)

    override fun bindView(item: FilmListItem, payloads: MutableList<Any>) {

      // Retrieve model.
      val model = item.model

      // Update view.
      titleText.text = model.title
      directorText.text = model.director
      producerText.text = model.producer

//        button.setOnClickListener{
//        var bool : Boolean = true
//        for (item in Cart.cartList)
//        {
//          if(item.title == model.title) {
//            item.quantity++
//            bool = false
//          }
//        }
//        if(bool) {
//          val item = CartDto(model.url, model.title, 1)
//          Cart.setInfoItem(item)
//        }
//        Snackbar.make(itemView, R.string.cartSuccess, Snackbar.LENGTH_SHORT).show()
//      }
    }

    override fun unbindView(item: FilmListItem) {
      titleText.text = null
      directorText.text = null
      producerText.text = null
    }
  }
}