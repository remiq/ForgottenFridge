package pl.mirkoczat.forgottenfridge

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.jetbrains.anko.textColor
import org.joda.time.DateTime
import org.joda.time.Days

class ProductAdapter(context: Context): ArrayAdapter<Product>(context, 0) {
    val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: inflater.inflate(R.layout.item_product, parent, false)

        val textExpire = view.findViewById(R.id.textExpire) as TextView
        val days = Days.daysBetween(DateTime.now().withTimeAtStartOfDay(),
                DateTime.parse(item.expire)).days
        textExpire.text = when (days) {
            0 -> view.resources.getString(R.string.zero_days)
            else -> view.resources.getQuantityString(R.plurals.days, days, days)
        }
        textExpire.textColor = when {
            days > 0 -> Color.BLACK
            else -> Color.RED
        }

        val textName = view.findViewById(R.id.textName) as TextView
        textName.text = item.name

        val textAmount = view.findViewById(R.id.textAmount) as TextView
        textAmount.text = item.amount
        when (item.amount.length) {
            0 -> textAmount.visibility = View.GONE
            else -> textAmount.visibility = View.VISIBLE
        }

        return view
    }
}
