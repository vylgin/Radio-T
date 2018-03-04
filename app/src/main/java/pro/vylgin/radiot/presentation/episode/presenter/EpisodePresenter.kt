package pro.vylgin.radiot.presentation.episode.presenter

import android.os.Build
import com.arellomobile.mvp.InjectViewState
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.extension.getTransitionNames
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.isEmpty
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.model.interactor.player.PlayerInteractor
import pro.vylgin.radiot.presentation.episode.EpisodeContract
import pro.vylgin.radiot.presentation.episode.view.EpisodeView
import pro.vylgin.radiot.presentation.global.presenter.BasePresenter
import pro.vylgin.radiot.presentation.global.presenter.ErrorHandler
import pro.vylgin.radiot.toothpick.PrimitiveWrapper
import pro.vylgin.radiot.toothpick.qualifier.EpisodeNumber
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class EpisodePresenter @Inject constructor(
        private var episode: Entry,
        @EpisodeNumber private val episodeNumberWrapper: PrimitiveWrapper<Int>?,
        private val router: Router,
        private val entriesInteractor: EntriesInteractor,
        private val playerInteractor: PlayerInteractor,
        private val errorHandler: ErrorHandler
) : BasePresenter<EpisodeView>(), EpisodeContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadEpisode()
    }

    override fun loadEpisode() {
        if (episode.isEmpty()) {
            entriesInteractor.getEpisode(episodeNumberWrapper?.value ?: -1)
                    .doOnSubscribe { viewState.showProgress(true) }
                    .doAfterTerminate { viewState.showProgress(false) }
                    .subscribe(
                            {
                                episode = it
                                showEpisode()
                                showTimeLabelsOrShowNotes()
                            },
                            { errorHandler.proceed(it, { viewState.showMessage(it) }) }
                    ).connect()
        } else {
            showEpisode()
        }
    }

    private fun showEpisode() {
        viewState.run {
            episode.apply {
                showToolbarTitle(title)
                val (imageViewTransitionName, titleTransitionName, dateTransitionName) = getTransitionNames()
                showToolbarImage(image, imageViewTransitionName)
                showEpisodeInfo(episode.title, episode.date.humanTime(), titleTransitionName, dateTransitionName)

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    showTimeLabelsOrShowNotes()
                }
            }
        }
    }

    override fun onMenuClick() = onBackPressed()
    override fun onBackPressed() = router.exit()

    override fun showTimeLabelsOrShowNotes() {
        if (episode.timeLabels != null) {
            episode.timeLabels?.let { viewState.showTimeLabels(it) }
        } else if (episode.showNotes != null) {
            episode.showNotes?.let { viewState.showEpisodeShowNotes(it) }
        }
    }

    override fun playEpisode() {
        playerInteractor.playEpisode(episode)
    }

    override fun seekTo(timeLabel: TimeLabel) {
        playerInteractor.seekTo(episode, timeLabel)
    }

}


