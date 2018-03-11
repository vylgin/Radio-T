package pro.vylgin.radiot.presentation.player.presenter

import com.arellomobile.mvp.InjectViewState
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
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

    private val observer = object : Observer<PlayerState> {
        override fun onComplete() {

        }

        override fun onSubscribe(disposable: Disposable) {
            disposable.connect()
        }

        override fun onNext(playerState: PlayerState) {
            when (playerState) {
                PlayerState.STOPPED -> {
                    viewState.showPlayButton()
                }
                PlayerState.PAUSED -> {
                    viewState.showPlayButton()
                }
                PlayerState.PLAYING -> {
                    viewState.showPauseButton()
                    playerInteractor.getPlayerObserver()
                            .subscribe {
                                checkSeek(it)
                            }.connect()
                }
            }

            Timber.d("playerState = $playerState")
        }

        override fun onError(error: Throwable) {
            Timber.e(error)
        }
    }

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
        checkNeedShowPlayerPanel()
        updateEpisodeInfo()
        bindPlayerService()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindPlayerService()
    }

    override fun bindPlayerService() {
        playerInteractor.bindPlayerService {
            playerInteractor.addPlayerStateObserver(observer)
        }
    }

    override fun unbindPlayerService() {
        playerInteractor.unbindPlayerService()
    }

    override fun onBackPressed() {}

    override fun checkSeek(seekModel: SeekModel) {
        val currentEpisode = playerInteractor.getCurrentEpisode()

        if (episode != currentEpisode) {
            episode = currentEpisode
            checkNeedShowPlayerPanel()
            updateEpisodeInfo()
        }

        viewState.apply {
            updateSeek(
                    seekModel.currentPositionInSeconds,
                    seekModel.buffer,
                    seekModel.currentPositionTextFormatted
            )
            updateDuration(seekModel.durationTextFormatted, seekModel.durationInSeconds)
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

    override fun updateCurrentTimeLabel(seekModel: SeekModel) {
        val timeLabel = seekModel.currentTimeLabel

        if (currentTimeLabel != timeLabel) {
            currentTimeLabel = timeLabel

            val timeLabelPosition = seekModel.currentTimeLabelPosition
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


