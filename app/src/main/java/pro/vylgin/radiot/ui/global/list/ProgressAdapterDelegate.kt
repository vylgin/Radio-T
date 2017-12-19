package pro.vylgin.radiot.ui.global.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import pro.vylgin.radiot.R
import pro.vylgin.radiot.extension.inflate


class ProgressAdapterDelegate : AdapterDelegate<MutableList<ListItem>>() {

    override fun isForViewType(items: MutableList<ListItem>, position: Int) =
            items[position] is ListItem.ProgressItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ProgressViewHolder(parent.inflate(R.layout.item_progress))

    override fun onBindViewHolder(items: MutableList<ListItem>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) {
    }

    private class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view)
}