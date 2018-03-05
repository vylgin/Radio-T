package pro.vylgin.radiot.presentation.player.presenter

import com.arellomobile.mvp.InjectViewState
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.model.interactor.player.PlayerInteractor
import pro.vylgin.radiot.presentation.global.presenter.BasePresenter
import pro.vylgin.radiot.presentation.player.PlayerContract
import pro.vylgin.radiot.presentation.player.view.PlayerView
import javax.inject.Inject

@InjectViewState
class PlayerPresenter @Inject constructor(
        private val playerInteractor: PlayerInteractor
) : BasePresenter<PlayerView>(), PlayerContract.Presenter {

    private var episode: Entry? = null
    private var currentTimeLabel: TimeLabel? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        init()
    }

    override fun init() {
        episode = playerInteractor.getCurrentEpisode()

        checkNeedShowPlayerPanel()
        updateEpisodeInfo()
        bindPlayerService()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindPlayerService()
    }

    override fun bindPlayerService() {
        playerInteractor.bindPlayerService()
    }

    override fun unbindPlayerService() {
        playerInteractor.unbindPlayerService()
    }

    override fun onBackPressed() {}

    override fun checkSeek() {
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

    override fun updateEpisodeInfo() {
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

    override fun checkNeedShowPlayerPanel() {
        if (episode == null) {
            viewState.hidePlayerPanel()
        } else {
            viewState.showPlayerPanel()
        }
    }

    override fun showTimeLabelsOrShowNotes() {
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

    override fun updateCurrentTimeLabel() {
        val timeLabel = playerInteractor.getCurrentTimeLabel()

        if (currentTimeLabel != timeLabel) {
            currentTimeLabel = timeLabel

            val timeLabelPosition = playerInteractor.getCurrentTimeLabelPosition()
            viewState.highlightCurrentTimeLabel(timeLabelPosition)

            val timeLableTitle = currentTimeLabel?.topic ?: ""
            viewState.updateCurrentTimeLabelTitle(timeLableTitle)
        }
    }

    override fun seekTo(positionMs: Long) {
        playerInteractor.seekTo(positionMs)
    }

    override fun seekTo(timeLabel: TimeLabel) {
        val episode = episode ?: return
        playerInteractor.seekTo(episode, timeLabel)
    }

    override fun playEpisode() {
        playerInteractor.playCurrentEpisode()
        viewState.showPauseButton()
    }

    override fun pauseEpisode() {
        playerInteractor.pauseEpisode()
        viewState.showPlayButton()
    }

    override fun playNextTopic() {
        playerInteractor.playNextTimeLabel()
    }

    override fun playPrevTopic() {
        playerInteractor.playPrevTimeLabel()
    }

}


