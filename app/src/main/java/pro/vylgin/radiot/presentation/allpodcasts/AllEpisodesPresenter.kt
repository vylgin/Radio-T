package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import pro.vylgin.radiot.Screens
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.presentation.global.ErrorHandler
import pro.vylgin.radiot.presentation.global.GlobalMenuController
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class AllEpisodesPresenter @Inject constructor(
        private val router: Router,
        private val entriesInteractor: EntriesInteractor,
        private val menuController: GlobalMenuController,
        private val errorHandler: ErrorHandler
) : MvpPresenter<AllEpisodesView>() {

    private var lastEpisodeNumber: Int = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        refreshEntries()
    }

    fun onMenuClick() = menuController.open()
    fun onBackPressed() = router.exit()

    fun refreshEntries() = entriesInteractor.getLastEntry().subscribe(
            {
                viewState.showRefreshProgress(false)

                lastEpisodeNumber = it

                onDescPressed()
            },
            {
                viewState.showRefreshProgress(false)
                errorHandler.proceed(it, { viewState.showMessage(it) })
            }
    )

    fun onEpisodeClicked(episodeNumber: Int) {
        router.navigateTo(Screens.PODCAST_SCREEN, episodeNumber)
    }

    fun onAscPressed() {
        val episodeNumbers = getEpisodeNumbers(true)
        viewState.showEpisodes(episodeNumbers)
    }

    fun onDescPressed() {
        val episodeNumbers = getEpisodeNumbers(false)
        viewState.showEpisodes(episodeNumbers)
    }

    private fun getEpisodeNumbers(isAsc: Boolean): List<Int> {
        val episodeNumbers = (1..lastEpisodeNumber).toList()
        return if (isAsc) {
            episodeNumbers
        } else {
            episodeNumbers.reversed()
        }
    }

}