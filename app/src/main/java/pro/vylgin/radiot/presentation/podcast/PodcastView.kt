package pro.vylgin.radiot.presentation.podcast

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel


@StateStrategyType(AddToEndSingleStrategy::class)
interface PodcastView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(message: String)

    fun showProgress(show: Boolean)
    fun showToolbarTitle(title: String)
    fun showToolbarImage(imageUrl: String?, transitionName: String = "")
    fun showPodcastInfo(podcast: Entry, titleTransitionName: String = "", dateTransitionName: String = "")
    fun showPodcastShowNotes(showNotes: String)
    fun showTimeLabels(timeLabels: List<TimeLabel>)
}