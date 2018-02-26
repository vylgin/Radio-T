package pro.vylgin.radiot.ui.global.list

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_news.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.getTransitionNames
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.inflate

class NewsAdapterDelegate(private val clickListener: (EntrySharedElement) -> Unit) : AdapterDelegate<MutableList<ListItem>>() {

    override fun isForViewType(items: MutableList<ListItem>, position: Int) =
            items[position] is ListItem.NewsItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            NewsViewHolder(parent.inflate(R.layout.item_news), clickListener)

    override fun onBindViewHolder(items: MutableList<ListItem>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as NewsViewHolder).bind((items[position] as ListItem.NewsItem).entry)

    private class NewsViewHolder(val view: View, clickListener: (EntrySharedElement) -> Unit) : RecyclerView.ViewHolder(view) {
        private lateinit var news: Entry

        init {
            view.setOnClickListener {
                val entrySharedElement = EntrySharedElement(news, null, view.titleTV, view.dateTV)
                clickListener.invoke(entrySharedElement)
            }
        }

        fun bind(news: Entry) {
            this.news = news

            val (titleTransitionName, dateTransitionName) = news.getTransitionNames()
            ViewCompat.setTransitionName(view.titleTV, titleTransitionName)
            ViewCompat.setTransitionName(view.dateTV, dateTransitionName)

            view.titleTV.text = news.title
            view.showNotesTV.text = news.showNotes
            view.dateTV.text = news.date.humanTime()
        }
    }
}