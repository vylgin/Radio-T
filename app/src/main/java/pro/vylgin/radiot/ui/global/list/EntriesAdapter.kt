package pro.vylgin.radiot.ui.global.list

import android.support.v7.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import pro.vylgin.radiot.entity.Category
import pro.vylgin.radiot.entity.Entry

//val callback: EntriesCallback -> Unit

class EntriesAdapter : ListDelegationAdapter<MutableList<ListItem>>() {

    interface EntriesCallback {
        fun onEpisodeClicked(entry: EntrySharedElement)
        fun onPrepClicked(entry: Entry)
        fun onNewsClicked(entry: EntrySharedElement)
        fun onInfoClicked(entry: EntrySharedElement)
    }

    private lateinit var callback: EntriesCallback

    init {
        items = mutableListOf()
        delegatesManager.addDelegate(EpisodeAdapterDelegate({ callback.onEpisodeClicked(it) }))
        delegatesManager.addDelegate(PrepAdapterDelegate({ callback.onPrepClicked(it) }))
        delegatesManager.addDelegate(NewsAdapterDelegate({ callback.onNewsClicked(it) }))
        delegatesManager.addDelegate(InfoAdapterDelegate({ callback.onInfoClicked(it) }))
        delegatesManager.addDelegate(ProgressAdapterDelegate())
    }

    fun setSearchCallback(callback: EntriesCallback) {
        this.callback = callback
    }

    fun setData(entries: List<Entry>) {
        val progress = isProgress()

        items.clear()
        items.addAll(entries.map {
            when (it.categories[0]) {
                Category.PODCAST -> ListItem.EpisodeItem(it)
                Category.PREP -> ListItem.PrepItem(it)
                Category.NEWS -> ListItem.NewsItem(it)
                Category.INFO -> ListItem.InfoItem(it)
                Category.SPECIAL -> ListItem.EpisodeItem(it)
            }
        })
        if (progress) items.add(ListItem.ProgressItem())

        notifyDataSetChanged()
    }

    fun showProgress(isVisible: Boolean) {
        val currentProgress = isProgress()

        if (isVisible && !currentProgress) items.add(ListItem.ProgressItem())
        else if (!isVisible && currentProgress) items.remove(items.last())

        notifyDataSetChanged()
    }

    private fun isProgress() = items.isNotEmpty() && items.last() is ListItem.ProgressItem

     override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
        super.onBindViewHolder(holder, position, payloads)

//            if (position == items.size - 10) presenter.loadNextProjectsPage()
    }
}