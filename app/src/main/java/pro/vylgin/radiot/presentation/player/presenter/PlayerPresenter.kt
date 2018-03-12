package pro.vylgin.radiot.presentation.player.presenter

import com.arellomobile.mvp.InjectViewState
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.model.data.player.PlayerState
import pro.vylgin.radiot.model.data.player.SeekModel
import pro.vylgin.radiot.model.interactor.player.PlayerInteractor
import pro.vylgin.radiot.presentation.global.presenter.BasePresenter
import pro.vylgin.radiot.presentation.player.PlayerContract
import pro.vylgin.radiot.presentation.player.view.PlayerView
import timber.log.Timber
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
        playerInteractor.getLastPlayedEpisode()
                .subscribe(
                        {
                            episode = it
                            initPlayer()
                        },
                        {
                            initPlayer()
                        }
                ).connect()
    }

    private fun initPlayer() {
        bindPlayerService()
        checkNeedShowPlayerPanel()
        updateEpisodeInfo()
    }

    override fun bindPlayerService() {
        playerInteractor.bindPlayerService {
            playerInteractor.getPlayerStateObservable()
                    .subscribe {
                        val playerState = it ?: return@subscribe
                        Timber.d("playerState = $playerState")
                        proceedPlayerState(playerState)
                    }.connect()
        }
    }

    private fun proceedPlayerState(playerState: PlayerState) {
        when (playerState) {
            PlayerState.STOPPED -> {
                viewState.showPlayButton()
                checkSeek(playerInteractor.getSavedSeedModel())
            }
            PlayerState.PAUSED -> {
                viewState.showPlayButton()
            }
            PlayerState.PLAYING -> {
                viewState.showPauseButton()
                playerInteractor.getPlayerObservable()
                        .subscribe {
                            checkSeek(it)
                        }.connect()
            }
        }
    }

    override fun unbindPlayerService() {
        playerInteractor.unbindPlayerService()
    }

    override fun checkNeedShowPlayerPanel() {
        if (episode == null) {
            viewState.hidePlayerPanel()
        } else {
            viewState.showPlayerPanel()
        }
    }

    override fun checkSeek(seekModel: SeekModel) {
        val currentEpisode = playerInteractor.getCurrentEpisode()

        if (episode != currentEpisode) {
            episode = currentEpisode
            checkNeedShowPlayerPanel()
            updateEpisodeInfo()
        }

        viewState.apply {
            updateDuration(seekModel.durationTextFormatted, seekModel.durationInSeconds)
            updateSeek(
                    seekModel.currentPositionInSeconds,
                    seekModel.buffer,
                    seekModel.currentPositionTextFormatted
            )
            updateCurrentTimeLabel(seekModel)
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

    override fun updateCurrentTimeLabel(seekModel: SeekModel) {
        val timeLabelPosition = seekModel.currentTimeLabelPosition
        viewState.highlightCurrentTimeLabel(timeLabelPosition)

        val timeLabel = seekModel.currentTimeLabel
        if (currentTimeLabel != timeLabel) {
            currentTimeLabel = timeLabel

            val timeLabelTitle = currentTimeLabel?.topic ?: ""
            viewState.updateCurrentTimeLabelTitle(timeLabelTitle)
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

    override fun onDestroy() {
        super.onDestroy()
        unbindPlayerService()
    }

    override fun onBackPressed() {}

}


