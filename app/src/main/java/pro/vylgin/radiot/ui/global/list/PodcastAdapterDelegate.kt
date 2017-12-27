package pro.vylgin.radiot.ui.global.list

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_podcast.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.getTransitionNames
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.inflate
import pro.vylgin.radiot.extension.loadImage


class PodcastAdapterDelegate(private val clickListener: (EntrySharedElement) -> Unit) : AdapterDelegate<MutableList<ListItem>>() {

    override fun isForViewType(items: MutableList<ListItem>, position: Int) =
            items[position] is ListItem.PodcastItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            PodcastViewHolder(parent.inflate(R.layout.item_podcast), clickListener)

    override fun onBindViewHolder(items: MutableList<ListItem>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as PodcastViewHolder).bind((items[position] as ListItem.PodcastItem).entry)

    private class PodcastViewHolder(val view: View, clickListener: (EntrySharedElement) -> Unit) : RecyclerView.ViewHolder(view) {
        private lateinit var podcast: Entry

        init {
            view.setOnClickListener {
                val entrySharedElement = EntrySharedElement(podcast, view.imageView, view.titleTV, view.dateTV)
                clickListener.invoke(entrySharedElement)
            }
        }

        fun bind(podcast: Entry) {
            this.podcast = podcast

            val (imageViewTransitionName, titleTransitionName, dateTransitionName) = podcast.getTransitionNames()
            ViewCompat.setTransitionName(view.imageView, imageViewTransitionName)
            ViewCompat.setTransitionName(view.titleTV, titleTransitionName)
            ViewCompat.setTransitionName(view.dateTV, dateTransitionName)

            view.titleTV.text = podcast.title
            view.showNotesTV.text = podcast.showNotes
            view.dateTV.text = podcast.date.humanTime()
            view.imageView.loadImage(podcast.image)
        }
    }
}