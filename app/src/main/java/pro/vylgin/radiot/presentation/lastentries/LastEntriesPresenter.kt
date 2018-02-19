package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.Single
import pro.vylgin.radiot.Screens
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.presentation.global.ErrorHandler
import pro.vylgin.radiot.presentation.global.GlobalMenuController
import pro.vylgin.radiot.presentation.global.Paginator
import pro.vylgin.radiot.ui.global.list.EntrySharedElement
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class LastEntriesPresenter @Inject constructor(
        private val router: Router,
        private val entriesInteractor: EntriesInteractor,
        private val menuController: GlobalMenuController,
        private val errorHandler: ErrorHandler
) : MvpPresenter<LastEntriesView>() {

    private var searchQuery = ""

    private val lastEntriesRequest: (Int) -> Single<List<Entry>> = { entriesInteractor.getEntries() }
    private val searchRequest: (Int) -> Single<List<Entry>> = { entriesInteractor.search(searchQuery) }

    private val paginator = Paginator(
            lastEntriesRequest,
            object : Paginator.ViewController<Entry> {
                override fun showEmptyProgress(show: Boolean) {
                    viewState.showEmptyProgress(show)
                }

                override fun showEmptyError(show: Boolean, error: Throwable?) {
                    if (error != null) {
                        errorHandler.proceed(error, { viewState.showEmptyError(show, it) })
                    } else {
                        viewState.showEmptyError(show, null)
                    }
                }

                override fun showErrorMessage(error: Throwable) {
                    errorHandler.proceed(error, { viewState.showMessage(it) })
                }

                override fun showEmptyView(show: Boolean) {
                    viewState.showEmptyView(show)
                }

                override fun showData(show: Boolean, data: List<Entry>) {
                    viewState.showEntries(show, data)
                }

                override fun showRefreshProgress(show: Boolean) {
                    viewState.showRefreshProgress(show)
                }

                override fun showPageProgress(show: Boolean) {
                    viewState.showPageProgress(show)
                }
            }
    )

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        refreshEntries()
    }

    fun onMenuClick() = menuController.open()

    fun pressStartSearchButton() {
        paginator.requestFactory = searchRequest
    }

    fun search(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            this.searchQuery = searchQuery
            refreshEntries()
        }
    }

    fun pressStopSearchButton() {
        paginator.requestFactory = lastEntriesRequest
        refreshEntries()
    }

    fun refreshEntries() = paginator.refresh()
    fun loadNextEventsPage() = paginator.loadNewPage()
    fun onBackPressed() = router.exit()

    fun onEpisodeClicked(entrySharedElement: EntrySharedElement) {
        router.navigateTo(Screens.EPISODE_SCREEN, entrySharedElement)
    }

    fun onPrepClicked(prep: Entry) {
        router.newScreenChain(Screens.PREP_SCREEN, prep)
    }

    fun onNewsClicked(entrySharedElement: EntrySharedElement) {
        router.navigateTo(Screens.NEWS_SCREEN, entrySharedElement)
    }

    fun onInfoClicked(entrySharedElement: EntrySharedElement) {
        router.navigateTo(Screens.INFO_SCREEN, entrySharedElement)
    }

}