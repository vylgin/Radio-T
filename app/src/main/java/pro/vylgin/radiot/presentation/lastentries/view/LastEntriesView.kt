package pro.vylgin.radiot.presentation.lastentries.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.presentation.lastentries.LastEntriesContract


@StateStrategyType(AddToEndSingleStrategy::class)
interface LastEntriesView : MvpView, LastEntriesContract.View {

}