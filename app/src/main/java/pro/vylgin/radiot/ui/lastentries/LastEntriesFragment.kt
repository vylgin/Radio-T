package pro.vylgin.radiot.ui.lastentries

import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_last_entries.*
import kotlinx.android.synthetic.main.layout_base_list.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.color
import pro.vylgin.radiot.extension.visible
import pro.vylgin.radiot.presentation.lastentries.LastEntriesPresenter
import pro.vylgin.radiot.presentation.lastentries.LastEntriesView
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.ui.global.BaseFragment
import pro.vylgin.radiot.ui.global.list.EntriesAdapter
import pro.vylgin.radiot.ui.global.list.EntrySharedElement
import toothpick.Toothpick


class LastEntriesFragment : BaseFragment(), LastEntriesView {

    override val layoutRes = R.layout.fragment_last_entries

    @InjectPresenter
    lateinit var presenter: LastEntriesPresenter

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

        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener { presenter.onMenuClick() }
        toolbar.inflateMenu(R.menu.menu_last_entries_fragment)

        val searchMenuItem = toolbar.menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search_item)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                presenter.search(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        searchView.setOnSearchClickListener {  }
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                presenter.pressSearchButton()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                presenter.pressStopSearchButton()
                return true
            }

        })
    }

    private fun initRecyclerView() {
        swipeToRefresh.setOnRefreshListener { presenter.refreshEntries() }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        adapter.setSearchCallback(object : EntriesAdapter.EntriesCallback {
            override fun onEpisodeClicked(entry: EntrySharedElement) {
                presenter.onEpisodeClicked(entry)
            }

            override fun onPrepClicked(entry: Entry) {
                presenter.onPrepClicked(entry)
            }

            override fun onNewsClicked(entry: EntrySharedElement) {
                presenter.onNewsClicked(entry)
            }

            override fun onInfoClicked(entry: EntrySharedElement) {
                presenter.onNewsClicked(entry)
            }

        })
        recyclerView.adapter = adapter
    }

    override fun showRefreshProgress(show: Boolean) {
        swipeToRefresh.post { swipeToRefresh?.isRefreshing = show }
    }

    override fun showEmptyProgress(show: Boolean) {
        fullscreenPV.visible(show)

        //trick for disable and hide swipeToRefresh on fullscreen progress
        swipeToRefresh.visible(!show)
        swipeToRefresh.post { swipeToRefresh.isRefreshing = false }
    }

    override fun showPageProgress(show: Boolean) {
        recyclerView.post { adapter.showProgress(isVisible) }
    }

    override fun showEmptyView(show: Boolean) {
        if (show) {
            emptySearchFL.visibility = View.VISIBLE
        } else {
            emptySearchFL.visibility = View.GONE
        }
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
}