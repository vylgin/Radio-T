package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.ui.global.list.EntrySharedElement

interface LastEntriesContract {

    interface View {
        fun showRefreshProgress(show: Boolean)
        fun showEmptyProgress(show: Boolean)
        fun showPageProgress(show: Boolean)
        fun showEmptyView(show: Boolean)
        fun showEmptyError(show: Boolean, message: String?)
        fun showEntries(show: Boolean, entries: List<Entry>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showMessage(message: String)
    }

    interface Presenter {
        fun onFirstViewAttach()
        fun onMenuClick()
        fun pressStartSearchButton()
        fun search(searchQuery: String)
        fun pressStopSearchButton()
        fun refreshEntries()
        fun loadNextEventsPage()
        fun onBackPressed()
        fun onEpisodeClicked(entrySharedElement: EntrySharedElement)
        fun onPrepClicked(prep: Entry)
        fun onNewsClicked(entrySharedElement: EntrySharedElement)
        fun onInfoClicked(entrySharedElement: EntrySharedElement)
    }

}