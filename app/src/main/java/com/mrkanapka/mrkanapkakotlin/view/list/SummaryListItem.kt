package com.mrkanapka.mrkanapkakotlin.view.list

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mrkanapka.mrkanapkakotlin.R
import com.mrkanapka.mrkanapkakotlin.api.model.Response.ResponseCartDetail
import java.util.*



class SummaryListItem(model: ResponseCartDetail) : ModelAbstractItem<ResponseCartDetail, SummaryListItem, SummaryListItem.SummaryListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.cart_type_id
    }

    override fun getViewHolder(v: View): SummaryListItemViewHolder {
        return SummaryListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_in_summary
    }

    override fun getIdentifier(): Long {
        return Objects.hash(model).toLong()
    }

    class SummaryListItemViewHolder(itemView: View) : FastAdapter.ViewHolder<SummaryListItem>(itemView) {

        private val titleText: TextView = itemView.findViewById(R.id.summaryName)
        private val textViewPrice: TextView = itemView.findViewById(R.id.summaryPrice)
        private val textViewAmount: TextView = itemView.findViewById(R.id.summaryAmount)
        //private val button: Button = itemView.findViewById(R.id.button2)

        @SuppressLint("SetTextI18n")
        override fun bindView(item: SummaryListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model
            // Update view.
            titleText.text = model.name
            textViewPrice.text = "%.2f zł".format(model.price.toDouble())
            textViewAmount.text = "${model.amount}"
        }

        override fun unbindView(item: SummaryListItem) {
            titleText.text = null
        }
    }
}