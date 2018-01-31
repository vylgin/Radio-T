package pro.vylgin.radiot.presentation.podcast

import android.os.Build
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.extension.addTo
import pro.vylgin.radiot.extension.getTransitionNames
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.isEmpty
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.model.interactor.player.PlayerInteractor
import pro.vylgin.radiot.presentation.global.ErrorHandler
import pro.vylgin.radiot.toothpick.PrimitiveWrapper
import pro.vylgin.radiot.toothpick.qualifier.PodcastNumber
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class PodcastPresenter @Inject constructor(
        private val podcast: Entry,
        @PodcastNumber private val podcastNumberWrapper: PrimitiveWrapper<Int>?,
        private val router: Router,
        private val entriesInteractor: EntriesInteractor,
        private val playerInteractor: PlayerInteractor,
        private val errorHandler: ErrorHandler
) : MvpPresenter<PodcastView>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (podcast.isEmpty()) {
            entriesInteractor.getPodcast(podcastNumberWrapper?.value ?: -1)
                    .doOnSubscribe { viewState.showProgress(true) }
                    .doAfterTerminate { viewState.showProgress(false) }
                    .subscribe(
                            { showPodcast(it) },
                            { errorHandler.proceed(it, { viewState.showMessage(it) }) }
                    )
                    .addTo(compositeDisposable)
        } else {
            showPodcast(podcast)
        }
    }

    private fun showPodcast(podcast: Entry) {
        viewState.run {
            podcast.apply {
                showToolbarTitle(title)
                val (imageViewTransitionName, titleTransitionName, dateTransitionName) = getTransitionNames()
                showToolbarImage(image, imageViewTransitionName)
                showPodcastInfo(podcast.title, podcast.date.humanTime(), titleTransitionName, dateTransitionName)

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    transitionAnimationEnd()
                }

                if (this@PodcastPresenter.podcast.isEmpty()) {
                    showTimeLabelsOrShowNotes(podcast)
                }
            }
        }
    }

    fun onMenuClick() = onBackPressed()
    fun onBackPressed() = router.exit()

    fun transitionAnimationEnd() {
        if (podcast.url.isNotEmpty()) {
            showTimeLabelsOrShowNotes(podcast)
        }
    }

    private fun showTimeLabelsOrShowNotes(podcast: Entry) {
        if (podcast.timeLabels != null) {
            viewState.showTimeLabels(podcast.timeLabels)
        } else if (podcast.showNotes != null) {
            viewState.showPodcastShowNotes(podcast.showNotes)
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

    fun playPodcast() {
        playerInteractor.playPodcast(podcast)
    }

    fun seekTo(timeLabel: TimeLabel) {
        playerInteractor.seekTo(podcast, timeLabel)
    }

}


