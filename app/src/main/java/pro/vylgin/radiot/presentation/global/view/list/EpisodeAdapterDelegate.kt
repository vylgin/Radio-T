package pro.vylgin.radiot.ui.global.list

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_episode.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.getTransitionNames
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.inflate
import pro.vylgin.radiot.extension.loadImage


class EpisodeAdapterDelegate(private val clickListener: (EntrySharedElement) -> Unit) : AdapterDelegate<MutableList<ListItem>>() {

    override fun isForViewType(items: MutableList<ListItem>, position: Int) =
            items[position] is ListItem.EpisodeItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            EpisodeViewHolder(parent.inflate(R.layout.item_episode), clickListener)

    override fun onBindViewHolder(items: MutableList<ListItem>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as EpisodeViewHolder).bind((items[position] as ListItem.EpisodeItem).entry)

    private class EpisodeViewHolder(val view: View, clickListener: (EntrySharedElement) -> Unit) : RecyclerView.ViewHolder(view) {
        private lateinit var episode: Entry

        init {
            view.setOnClickListener {
                val entrySharedElement = EntrySharedElement(episode, view.imageView, view.titleTV, view.dateTV)
                clickListener.invoke(entrySharedElement)
            }
        }

        fun bind(episode: Entry) {
            this.episode = episode

            val (imageViewTransitionName, titleTransitionName, dateTransitionName) = episode.getTransitionNames()
            ViewCompat.setTransitionName(view.imageView, imageViewTransitionName)
            ViewCompat.setTransitionName(view.titleTV, titleTransitionName)
            ViewCompat.setTransitionName(view.dateTV, dateTransitionName)

            view.titleTV.text = episode.title
            view.showNotesTV.text = episode.showNotes
            view.dateTV.text = episode.date.humanTime()
            view.imageView.loadImage(episode.image)
        }
    }
}