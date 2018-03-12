package pro.vylgin.radiot.presentation.player

import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.model.data.player.SeekModel

interface PlayerContract {

    interface View {
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

    interface Presenter {
        fun init()
        fun bindPlayerService()
        fun unbindPlayerService()
        fun onBackPressed()
        fun checkSeek(seekModel: SeekModel)
        fun updateEpisodeInfo()
        fun checkNeedShowPlayerPanel()
        fun showTimeLabelsOrShowNotes()
        fun updateCurrentTimeLabel(seekModel: SeekModel)
        fun seekTo(positionMs: Long)
        fun seekTo(timeLabel: TimeLabel)
        fun playEpisode()
        fun pauseEpisode()
        fun playNextTopic()
        fun playPrevTopic()
    }

}