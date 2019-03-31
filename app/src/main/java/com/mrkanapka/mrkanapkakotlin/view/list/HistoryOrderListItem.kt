package com.mrkanapka.mrkanapkakotlin.view.list

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseHistoryList
import java.util.*



class HistoryOrderListItem(model: ResponseHistoryList) : ModelAbstractItem<ResponseHistoryList, HistoryOrderListItem, HistoryOrderListItem.HistoryOrderListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.cart_type_id
    }

    override fun getViewHolder(v: View): HistoryOrderListItemViewHolder {
        return HistoryOrderListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_in_history
    }

    override fun getIdentifier(): Long {
        return Objects.hash(model).toLong()
    }

    class HistoryOrderListItemViewHolder(itemView: View) : FastAdapter.ViewHolder<HistoryOrderListItem>(itemView) {

        private val orderNumberText: TextView = itemView.findViewById(R.id.orderNumberTextView)
        private val dataOrderText: TextView = itemView.findViewById(R.id.dataOrderTextView)
        private val statusText: TextView = itemView.findViewById(R.id.statusTextView)
        private val priceText: TextView = itemView.findViewById(R.id.priceTextView)

        override fun bindView(item: HistoryOrderListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model
            // Update view.
            orderNumberText.text = model.order_number
            priceText.text = "%.2f zł".format(model.full_price.toDouble())
            statusText.text = model.status
            dataOrderText.text = model.date
        }

        override fun unbindView(item: HistoryOrderListItem) {
            orderNumberText.text = null
        }
    }
}