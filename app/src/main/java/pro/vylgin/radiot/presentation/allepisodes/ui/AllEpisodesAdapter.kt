package pro.vylgin.radiot.presentation.allpodcasts.ui

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_episode_mini.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.extension.inflate

class AllEpisodesAdapter(private val episodeNumberClickListener: (Int) -> Unit) : RecyclerView.Adapter<AllEpisodesAdapter.ViewHolder>() {

    private val items: MutableList<Int> = mutableListOf()

    fun initData(episodeNumbers: List<Int>) {
        items.clear()
        items.addAll(episodeNumbers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_episode_mini))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], episodeNumberClickListener)

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Int, listener: (Int) -> Unit) = with(itemView) {
            titleTV.text = "Выпуск $item"

            setOnClickListener { listener(item) }
        }
    }
}