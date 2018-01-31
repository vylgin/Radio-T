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

    private var podcast: Entry? = null
    private var currentTimeLabel: TimeLabel? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        podcast = playerInteractor.getCurrentPodcast()

        checkNeedShowPlayerPanel()
        updatePodcastInfo()

        playerInteractor.bindPlayerService()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.unbindPlayerService()
    }

    fun onBackPressed() {}

    fun checkSeek() {
        val currentPodcast = playerInteractor.getCurrentPodcast()

        if (podcast != currentPodcast) {
            podcast = currentPodcast
            checkNeedShowPlayerPanel()
            updatePodcastInfo()
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

    private fun updatePodcastInfo() {
        viewState.apply {
            updateTitle(podcast?.title ?: "")
            updateSmallImage(podcast?.image)
            showTimeLabelsOrShowNotes()
            if (podcast?.timeLabels == null) {
                hidePrevAndNextButtons()
                hideTimeLabelTitle()
            } else {
                showPrevAndNextButtons()
                showTimeLabelTitle()
            }
        }
    }

    private fun checkNeedShowPlayerPanel() {
        if (podcast == null) {
            viewState.hidePlayerPanel()
        } else {
            viewState.showPlayerPanel()
        }
    }

    private fun showTimeLabelsOrShowNotes() {
        val timeLabels = podcast?.timeLabels
        if (timeLabels != null) {
            viewState.showTimeLabels(timeLabels)
        } else {
            val showNotes = podcast?.showNotes
            if (showNotes != null) {
                viewState.showPodcastShowNotes(showNotes)
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
        val podcast = podcast ?: return
        playerInteractor.seekTo(podcast, timeLabel)
    }

    fun playPodcast() {
        playerInteractor.playCurrentPodcast()
        viewState.showPauseButton()
    }

    fun pausePodcast() {
        playerInteractor.pausePodcast()
        viewState.showPlayButton()
    }

    fun playNextTopic() {
        playerInteractor.playNextTimeLabel()
    }

    fun playPrevTopic() {
        playerInteractor.playPrevTimeLabel()
    }

}


