package pro.vylgin.radiot.presentation.lastentries.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.Single
import pro.vylgin.radiot.Screens
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.presentation.global.presenter.ErrorHandler
import pro.vylgin.radiot.presentation.global.presenter.GlobalMenuController
import pro.vylgin.radiot.presentation.global.presenter.Paginator
import pro.vylgin.radiot.presentation.lastentries.LastEntriesContract
import pro.vylgin.radiot.presentation.lastentries.view.LastEntriesView
import pro.vylgin.radiot.ui.global.list.EntrySharedElement
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class LastEntriesPresenter @Inject constructor(
        private val router: Router,
        private val entriesInteractor: EntriesInteractor,
        private val menuController: GlobalMenuController,
        private val errorHandler: ErrorHandler
) : MvpPresenter<LastEntriesView>(), LastEntriesContract.Presenter {

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

    override fun onMenuClick() = menuController.open()

    override fun pressStartSearchButton() {
        paginator.requestFactory = searchRequest
    }

    override fun search(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            this.searchQuery = searchQuery
            refreshEntries()
        }
    }

    override fun pressStopSearchButton() {
        paginator.requestFactory = lastEntriesRequest
        refreshEntries()
    }

    override fun refreshEntries() = paginator.refresh()
    override fun loadNextEventsPage() = paginator.loadNewPage()
    override fun onBackPressed() = router.exit()

    override fun onEpisodeClicked(entrySharedElement: EntrySharedElement) {
        router.navigateTo(Screens.EPISODE_SCREEN, entrySharedElement)
    }

    override fun onPrepClicked(prep: Entry) {
        router.newScreenChain(Screens.PREP_SCREEN, prep)
    }

    override fun onNewsClicked(entrySharedElement: EntrySharedElement) {
        router.navigateTo(Screens.NEWS_SCREEN, entrySharedElement)
    }

    override fun onInfoClicked(entrySharedElement: EntrySharedElement) {
        router.navigateTo(Screens.INFO_SCREEN, entrySharedElement)
    }

}