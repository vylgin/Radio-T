package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.entity.Entry


@StateStrategyType(AddToEndSingleStrategy::class)
interface LastEntriesView : MvpView {
    fun showRefreshProgress(show: Boolean)
    fun showEmptyProgress(show: Boolean)
    fun showPageProgress(show: Boolean)
    fun showEmptyView(show: Boolean)
    fun showEmptyError(show: Boolean, message: String?)
    fun showEntries(show: Boolean, entries: List<Entry>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(message: String)
}