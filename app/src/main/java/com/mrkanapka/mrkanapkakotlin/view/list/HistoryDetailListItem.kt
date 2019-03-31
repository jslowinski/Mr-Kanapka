package com.mrkanapka.mrkanapkakotlin.view.list

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseHistoryDetailProduct
import java.util.*



class HistoryDetailListItem(model: ResponseHistoryDetailProduct) : ModelAbstractItem<ResponseHistoryDetailProduct, HistoryDetailListItem, HistoryDetailListItem.HistoryDetailListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.cart_type_id
    }

    override fun getViewHolder(v: View): HistoryDetailListItemViewHolder {
        return HistoryDetailListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_in_history_detail
    }

    override fun getIdentifier(): Long {
        return Objects.hash(model).toLong()
    }

    class HistoryDetailListItemViewHolder(itemView: View) : FastAdapter.ViewHolder<HistoryDetailListItem>(itemView) {

        private val productName: TextView = itemView.findViewById(R.id.historyName)
        private val productPrice: TextView = itemView.findViewById(R.id.historyPrice)
        private val historyAmount: TextView = itemView.findViewById(R.id.historyAmount)
        private var id: Int = 0

        override fun bindView(item: HistoryDetailListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model
            // Update view.
            productName.text = model.name
            productPrice.text = "%.2f zł".format(model.price.toDouble())
            historyAmount.text = model.amount.toString()
            id = model.id
        }

        override fun unbindView(item: HistoryDetailListItem) {
            productName.text = null
        }
    }
}