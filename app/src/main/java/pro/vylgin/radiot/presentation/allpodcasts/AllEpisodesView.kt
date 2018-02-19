package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndSingleStrategy::class)
interface AllEpisodesView : MvpView {
    fun showRefreshProgress(show: Boolean)
    fun showEpisodes(episodeNumbers: List<Int>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(message: String)
    fun showSortSpinner(show: Boolean)
    fun enableRefreshLayout(enable: Boolean)
}