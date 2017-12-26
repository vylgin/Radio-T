package pro.vylgin.radiot.ui.global.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_prep.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.inflate

class PrepAdapterDelegate(private val clickListener: (Entry) -> Unit) : AdapterDelegate<MutableList<ListItem>>() {

    override fun isForViewType(items: MutableList<ListItem>, position: Int): Boolean =
            items[position] is ListItem.PrepItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        PrepViewHolder(parent.inflate(R.layout.item_prep), clickListener)

    override fun onBindViewHolder(items: MutableList<ListItem>, position: Int, viewHolder: RecyclerView.ViewHolder, payloads: MutableList<Any>) =
            (viewHolder as PrepViewHolder).bind((items[position] as ListItem.PrepItem).entry)

    private class PrepViewHolder(val view: View, clickListener: (Entry) -> Unit) : RecyclerView.ViewHolder(view) {
        private lateinit var prep : Entry

        init {
            view.setOnClickListener { clickListener.invoke(prep) }
        }

        fun bind(prep : Entry) {
            this.prep = prep

            view.titleTV.text = prep.title
            view.dateTV.text = prep.date.humanTime(view.resources)
            view.showNotesTV.text = prep.url
        }
    }

}