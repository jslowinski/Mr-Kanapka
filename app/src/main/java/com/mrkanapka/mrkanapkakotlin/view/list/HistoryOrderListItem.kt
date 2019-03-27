package com.mrkanapka.mrkanapkakotlin.view.list

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.model.ResponseCartDetail
import java.util.*



class HistoryOrderListItem(model: ResponseCartDetail) : ModelAbstractItem<ResponseCartDetail, HistoryOrderListItem, HistoryOrderListItem.HistoryOrderListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.cart_type_id
    }

    override fun getViewHolder(v: View): HistoryOrderListItemViewHolder {
        return HistoryOrderListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_in_summary
    }

    override fun getIdentifier(): Long {
        return Objects.hash(model).toLong()
    }

    class HistoryOrderListItemViewHolder(itemView: View) : FastAdapter.ViewHolder<HistoryOrderListItem>(itemView) {

        private val titleText: TextView = itemView.findViewById(R.id.summaryName)
        private val textViewPrice: TextView = itemView.findViewById(R.id.summaryPrice)

        override fun bindView(item: HistoryOrderListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model
            // Update view.
            titleText.text = model.name
            textViewPrice.text = "%.2f zł".format(model.price.toDouble())
        }

        override fun unbindView(item: HistoryOrderListItem) {
            titleText.text = null
        }
    }
}