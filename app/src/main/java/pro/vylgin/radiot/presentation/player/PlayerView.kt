package pro.vylgin.radiot.presentation.player

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.entity.TimeLabel


@StateStrategyType(AddToEndSingleStrategy::class)
interface PlayerView : MvpView {

    fun updateSeek(progress: Int, buffered: Int, currentTime: String)
    fun updateTitle(title: String)
    fun updateSmallImage(image: String?)
    fun updateDuration(duration: String, durationSec: Int)
    fun showTimeLabels(timeLabels: List<TimeLabel>)
    fun showEpisodeShowNotes(showNotes: String)
    fun highlightCurrentTimeLabel(timeLabelPosition: Int)
    fun updateCurrentTimeLabelTitle(topic: String)
    fun showTimeLabelTitle()
    fun hideTimeLabelTitle()
    fun showPlayButton()
    fun showPauseButton()
    fun showPrevAndNextButtons()
    fun hidePrevAndNextButtons()
    fun showPlayerPanel()
    fun hidePlayerPanel()

}