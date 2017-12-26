package pro.vylgin.radiot.ui.lastentries

import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import kotlinx.android.synthetic.main.fragment_last_entries.*
import kotlinx.android.synthetic.main.layout_base_list.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Category
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.color
import pro.vylgin.radiot.extension.visible
import pro.vylgin.radiot.presentation.lastentries.LastEntriesPresenter
import pro.vylgin.radiot.presentation.lastentries.LastEntriesView
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.ui.global.BaseFragment
import pro.vylgin.radiot.ui.global.list.*
import toothpick.Toothpick


class LastEntriesFragment : BaseFragment(), LastEntriesView {

    override val layoutRes = R.layout.fragment_last_entries

    @InjectPresenter lateinit var presenter: LastEntriesPresenter

    private val adapter = EntriesAdapter()

    @ProvidePresenter
    fun providePresenter(): LastEntriesPresenter {
        val scopeName = "last entries scope"
        val scope = Toothpick.openScopes(DI.MAIN_ACTIVITY_SCOPE, scopeName)
        return scope.getInstance(LastEntriesPresenter::class.java).also {
            Toothpick.closeScope(scopeName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor = resources.color(R.color.colorPrimaryDark)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationOnClickListener { presenter.onMenuClick() }

        initRecyclerView()

        swipeToRefresh.setOnRefreshListener { presenter.refreshEntries() }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.adapter = adapter
    }

    override fun showRefreshProgress(show: Boolean) {
        swipeToRefresh.post { swipeToRefresh?.isRefreshing = show }
    }

    override fun showEmptyProgress(show: Boolean) {
        fullscreenProgressView.visible(show)

        //trick for disable and hide swipeToRefresh on fullscreen progress
        swipeToRefresh.visible(!show)
        swipeToRefresh.post { swipeToRefresh.isRefreshing = false }
    }

    override fun showPageProgress(show: Boolean) {
        recyclerView.post { adapter.showProgress(isVisible) }
    }

    override fun showEmptyView(show: Boolean) {
        TODO("not implemented")
    }

    override fun showEmptyError(show: Boolean, message: String?) {
        if (show && message != null) showSnackMessage(message)
    }

    override fun showEntries(show: Boolean, entries: List<Entry>) {
        recyclerView.visible(show)
        recyclerView.post { adapter.setData(entries) }
    }

    override fun showMessage(message: String) {
        showSnackMessage(message)
    }

    override fun onBackPressed() = presenter.onBackPressed()

    inner class EntriesAdapter : ListDelegationAdapter<MutableList<ListItem>>() {
        init {
            items = mutableListOf()
            delegatesManager.addDelegate(PodcastAdapterDelegate({ presenter.onPodcastClicked(it) }))
            delegatesManager.addDelegate(PrepAdapterDelegate({ presenter.onPrepClicked(it) }))
            delegatesManager.addDelegate(NewsAdapterDelegate({ presenter.onNewsClicked(it) }))
            delegatesManager.addDelegate(InfoAdapterDelegate({ presenter.onInfoClicked(it) }))
            delegatesManager.addDelegate(ProgressAdapterDelegate())
        }

        fun setData(entries: List<Entry>) {
            val progress = isProgress()

            items.clear()
            items.addAll(entries.map {
                when (it.categories[0]) {
                    Category.PODCAST -> ListItem.PodcastItem(it)
                    Category.PREP -> ListItem.PrepItem(it)
                    Category.NEWS -> ListItem.NewsItem(it)
                    Category.INFO -> ListItem.InfoItem(it)
                    Category.SPECIAL -> ListItem.PodcastItem(it)
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
}