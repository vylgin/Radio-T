package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.entity.Entry


@StateStrategyType(AddToEndSingleStrategy::class)
interface LastEntriesView : MvpView {
    fun showEntries(entries: List<Entry>)
    fun showError(message: String)
}