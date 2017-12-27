package pro.vylgin.radiot.presentation.news

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndSingleStrategy::class)
interface NewsView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(message: String)

    fun showProgress(show: Boolean)
    fun showToolbarTitle(title: String)
    fun showNewsInfo(title: String, date: String, titleTransitionName: String = "", dateTransitionName: String = "")
    fun showNewsShowNotes(showNotes: String)
}