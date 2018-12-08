package com.swapi.swapikotlin.view.list

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter.ViewHolder
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.swapi.swapikotlin.R
import com.swapi.swapikotlin.api.model.PlanetDto
import com.swapi.swapikotlin.view.list.PlanetListItem.PlanetListItemViewHolder

import java.util.Objects.hash

class PlanetListItem(model: PlanetDto) : ModelAbstractItem<PlanetDto, PlanetListItem, PlanetListItemViewHolder>(model) {

  override fun getType(): Int {
    return R.id.planet_type_id
  }

  override fun getViewHolder(v: View): PlanetListItemViewHolder {
    return PlanetListItemViewHolder(v)
  }

  override fun getLayoutRes(): Int {
    return R.layout.item_planets
  }

  override fun getIdentifier(): Long {
    return hash(model).toLong()
  }

  class PlanetListItemViewHolder(itemView: View) : ViewHolder<PlanetListItem>(itemView) {

    private val nameText: TextView = itemView.findViewById(R.id.name)
    private val rotation_peroidText: TextView = itemView.findViewById(R.id.rotation)
    private val orbital_preoidText: TextView = itemView.findViewById(R.id.orbital)

    override fun bindView(item: PlanetListItem, payloads: MutableList<Any>) {

      // Retrieve model.
      val model = item.model

      // Update view.
      nameText.text = model.name
      rotation_peroidText.text = model.rotation_peroid
      orbital_preoidText.text = model.orbital_preoid
    }

    override fun unbindView(item: PlanetListItem) {
      nameText.text = null
      rotation_peroidText.text = null
      orbital_preoidText.text = null
    }

  }
}