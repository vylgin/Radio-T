package pro.vylgin.radiot.presentation.allepisodes

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface AllEpisodesContract {

    interface View {
        fun showRefreshProgress(show: Boolean)
        fun showEpisodes(episodeNumbers: List<Int>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showMessage(message: String)
        fun showSortSpinner(show: Boolean)
        fun enableRefreshLayout(enable: Boolean)
    }

    interface Presenter {
        fun onMenuClick()
        fun onBackPressed()
        fun swipeToRefresh()
        fun pressStartSearchButton()
        fun search(searchQuery: String)
        fun pressStopSearchButton()
        fun onEpisodeClicked(episodeNumber: Int)
        fun onAscPressed()
        fun onDescPressed()
        fun onDestroy()
    }

}