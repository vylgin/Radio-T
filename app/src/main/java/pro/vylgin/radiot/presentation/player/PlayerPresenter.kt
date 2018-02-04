package pro.vylgin.radiot.presentation.player

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.model.interactor.player.PlayerInteractor
import javax.inject.Inject

@InjectViewState
class PlayerPresenter @Inject constructor(
        private val playerInteractor: PlayerInteractor
) : MvpPresenter<PlayerView>() {

    private var episode: Entry? = null
    private var currentTimeLabel: TimeLabel? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        episode = playerInteractor.getCurrentEpisode()

        checkNeedShowPlayerPanel()
        updateEpisodeInfo()

        playerInteractor.bindPlayerService()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.unbindPlayerService()
    }

    fun onBackPressed() {}

    fun checkSeek() {
        val currentEpisode = playerInteractor.getCurrentEpisode()

        if (episode != currentEpisode) {
            episode = currentEpisode
            checkNeedShowPlayerPanel()
            updateEpisodeInfo()
        }

        if (playerInteractor.statePlaying) {
            viewState.apply {
                updateSeek(
                        playerInteractor.getProgress(),
                        playerInteractor.getBuffered(),
                        playerInteractor.getCurrentPosition()
                )
                updateDuration(playerInteractor.getDuration(), playerInteractor.getDurationSec())
                updateCurrentTimeLabel()
                showPauseButton()
            }
        } else {
            viewState.showPlayButton()
        }
    }

    private fun updateEpisodeInfo() {
        viewState.apply {
            updateTitle(episode?.title ?: "")
            updateSmallImage(episode?.image)
            showTimeLabelsOrShowNotes()
            if (episode?.timeLabels == null) {
                hidePrevAndNextButtons()
                hideTimeLabelTitle()
            } else {
                showPrevAndNextButtons()
                showTimeLabelTitle()
            }
        }
    }

    private fun checkNeedShowPlayerPanel() {
        if (episode == null) {
            viewState.hidePlayerPanel()
        } else {
            viewState.showPlayerPanel()
        }
    }

    private fun showTimeLabelsOrShowNotes() {
        val timeLabels = episode?.timeLabels
        if (timeLabels != null) {
            viewState.showTimeLabels(timeLabels)
        } else {
            val showNotes = episode?.showNotes
            if (showNotes != null) {
                viewState.showEpisodeShowNotes(showNotes)
            }
        }
    }

    private fun updateCurrentTimeLabel() {
        val timeLabel = playerInteractor.getCurrentTimeLabel()

        if (currentTimeLabel != timeLabel) {
            currentTimeLabel = timeLabel

            val timeLabelPosition = playerInteractor.getCurrentTimeLabelPosition()
            viewState.highlightCurrentTimeLabel(timeLabelPosition)

            val timeLableTitle = currentTimeLabel?.topic ?: ""
            viewState.updateCurrentTimeLabelTitle(timeLableTitle)
        }
    }

    fun seekTo(positionMs: Long) {
        playerInteractor.seekTo(positionMs)
    }

    fun seekTo(timeLabel: TimeLabel) {
        val episode = episode ?: return
        playerInteractor.seekTo(episode, timeLabel)
    }

    fun playEpisode() {
        playerInteractor.playCurrentEpisode()
        viewState.showPauseButton()
    }

    fun pauseEpisode() {
        playerInteractor.pauseEpisode()
        viewState.showPlayButton()
    }

    fun playNextTopic() {
        playerInteractor.playNextTimeLabel()
    }

    fun playPrevTopic() {
        playerInteractor.playPrevTimeLabel()
    }

}


