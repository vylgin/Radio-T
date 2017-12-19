package pro.vylgin.radiot.ui.global.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_podcast.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.inflate


class PodcastAdapterDelegate(private val clickListener: (Entry) -> Unit) : AdapterDelegate<MutableList<ListItem>>() {

    override fun isForViewType(items: MutableList<ListItem>, position: Int) =
            items[position] is ListItem.PodcastItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            PodcastViewHolder(parent.inflate(R.layout.item_podcast), clickListener)

    override fun onBindViewHolder(items: MutableList<ListItem>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as PodcastViewHolder).bind((items[position] as ListItem.PodcastItem).entry)

    private class PodcastViewHolder(val view: View, clickListener: (Entry) -> Unit) : RecyclerView.ViewHolder(view) {
        private lateinit var podcast: Entry

        init {
            view.setOnClickListener { clickListener.invoke(podcast) }
        }

        fun bind(podcast: Entry) {
            this.podcast = podcast

            view.titleTV.text = podcast.title
            view.urlTV.text = podcast.showNotes
            view.dateTV.text = podcast.date.humanTime(view.resources)

            val image: String = podcast.image ?: ""
            if (image.isNotEmpty()) {
                Glide.with(view.context)
                        .load(image)
                        .apply(RequestOptions().centerCrop())
                        .into(view.imageView)
            }
        }
    }
}