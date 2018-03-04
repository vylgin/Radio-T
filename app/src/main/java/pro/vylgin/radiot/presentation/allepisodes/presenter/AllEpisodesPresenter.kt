package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import pro.vylgin.radiot.Screens
import pro.vylgin.radiot.extension.addTo
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.presentation.allepisodes.AllEpisodesContract
import pro.vylgin.radiot.presentation.allpodcasts.AllEpisodesPresenterCache
import pro.vylgin.radiot.presentation.global.presenter.ErrorHandler
import pro.vylgin.radiot.presentation.global.presenter.GlobalMenuController
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class AllEpisodesPresenter @Inject constructor(
        private val router: Router,
        private val entriesInteractor: EntriesInteractor,
        private val allepisodesPresenterCache: AllEpisodesPresenterCache,
        private val menuController: GlobalMenuController,
        private val errorHandler: ErrorHandler
) : MvpPresenter<AllEpisodesView>(), AllEpisodesContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        refreshEpisodes()
    }

    override fun onMenuClick() = menuController.open()
    override fun onBackPressed() = router.exit()

    override fun swipeToRefresh() {
        viewState.showRefreshProgress(true)
        refreshEpisodes {
            viewState.showRefreshProgress(false)
        }
    }

    private fun refreshEpisodes(refreshFinishCallback: () -> Unit = {}) = entriesInteractor.getAllEpisodeNumbers().subscribe(
            {
                allepisodesPresenterCache.updateEpisodeNumbers(it)
                onDescPressed()
                refreshFinishCallback.invoke()
            },
            {
                errorHandler.proceed(it, { viewState.showMessage(it) })
                refreshFinishCallback.invoke()
            }
    ).addTo(compositeDisposable)

    override fun pressStartSearchButton() {
        viewState.showSortSpinner(false)
        viewState.enableRefreshLayout(false)
    }

    override fun search(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            viewState.showEpisodes(allepisodesPresenterCache.getEpisodeNumbers()
                    .filter { searchQuery.contains(it.toString()) }
                    .sortedDescending())
        }
    }

    override fun pressStopSearchButton() {
        viewState.showSortSpinner(true)
        refreshEpisodes()
        viewState.enableRefreshLayout(true)
    }

    override fun onEpisodeClicked(episodeNumber: Int) {
        router.navigateTo(Screens.EPISODE_SCREEN, episodeNumber)
    }

    override fun onAscPressed() {
        val episodeNumbers = allepisodesPresenterCache.getEpisodeNumbers()
        viewState.showEpisodes(episodeNumbers)
    }

    override fun onDescPressed() {
        val episodeNumbers = allepisodesPresenterCache.getEpisodeNumbers().reversed()
        viewState.showEpisodes(episodeNumbers)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}