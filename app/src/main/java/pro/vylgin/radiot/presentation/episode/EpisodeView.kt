package pro.vylgin.radiot.presentation.episode

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.entity.TimeLabel


@StateStrategyType(AddToEndSingleStrategy::class)
interface EpisodeView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(message: String)

    fun showProgress(show: Boolean)
    fun showToolbarTitle(title: String)
    fun showToolbarImage(imageUrl: String?, transitionName: String = "")
    fun showEpisodeInfo(title: String, date: String, titleTransitionName: String = "", dateTransitionName: String = "")
    fun showEpisodeShowNotes(showNotes: String)
    fun showTimeLabels(timeLabels: List<TimeLabel>)

}