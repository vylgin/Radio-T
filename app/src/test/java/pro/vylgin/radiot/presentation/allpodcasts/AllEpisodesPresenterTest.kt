package pro.vylgin.radiot.presentation.allpodcasts

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import pro.vylgin.radiot.Screens
import pro.vylgin.radiot.model.data.server.ServerError
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.presentation.global.presenter.ErrorHandler
import pro.vylgin.radiot.presentation.global.presenter.GlobalMenuController
import pro.vylgin.radiot.presentation.lastentries.AllEpisodesPresenter
import pro.vylgin.radiot.presentation.lastentries.AllEpisodesView
import ru.terrakok.cicerone.Router


class AllEpisodesPresenterTest {

    private lateinit var presenter: AllEpisodesPresenter

    private lateinit var router: Router
    private lateinit var entriesInteractor: EntriesInteractor
    private lateinit var allEpisodesPresenterCache: AllEpisodesPresenterCache
    private lateinit var menuController: GlobalMenuController
    private lateinit var errorHandler: ErrorHandler
    private lateinit var allEpisodesView: AllEpisodesView

    @Before
    fun setUp() {
        router = mock()
        entriesInteractor = mock()
        allEpisodesPresenterCache = mock()
        menuController = mock()
        errorHandler = mock()
        allEpisodesView = mock()

        val episodeNumbers = (1..15).toList()
        whenever(entriesInteractor.getAllEpisodeNumbers()).thenReturn(Single.just(episodeNumbers))

        presenter = AllEpisodesPresenter(router, entriesInteractor, allEpisodesPresenterCache, menuController, errorHandler)
        presenter.attachView(allEpisodesView)
    }

    @Test
    fun onMenuClick() {
        presenter.onMenuClick()
        verify(menuController).open()
    }

    @Test
    fun onBackPressed() {
        presenter.onBackPressed()
        verify(router).exit()
    }

    @Test
    fun refreshEpisodes_success() {
        val episodeNumbers = (1..12).toList()
        whenever(entriesInteractor.getAllEpisodeNumbers()).thenReturn(Single.just(episodeNumbers))
        whenever(allEpisodesPresenterCache.getEpisodeNumbers()).thenReturn(episodeNumbers)

        presenter.swipeToRefresh()

        verify(allEpisodesView).showRefreshProgress(true)
        verify(allEpisodesView).showRefreshProgress(false)

        verify(allEpisodesPresenterCache).updateEpisodeNumbers(episodeNumbers)
        verify(allEpisodesView).showEpisodes(episodeNumbers.reversed())
    }

    @Test
    fun refreshEpisodes_errorLoad() {
        val serverError = ServerError(404)
        whenever(entriesInteractor.getAllEpisodeNumbers()).thenReturn(Single.error(serverError))

        presenter.swipeToRefresh()

        verify(allEpisodesView).showRefreshProgress(true)
        verify(allEpisodesView).showRefreshProgress(false)

        verify(errorHandler).proceed(eq(serverError), any())
    }

    @Test
    fun pressStartSearchButton() {
        presenter.pressStartSearchButton()

        verify(allEpisodesView).showSortSpinner(false)
        verify(allEpisodesView).enableRefreshLayout(false)

        verify(allEpisodesView, never()).showSortSpinner(true)
        verify(allEpisodesView, never()).enableRefreshLayout(true)
    }

    @Test
    fun search() {
        whenever(allEpisodesPresenterCache.getEpisodeNumbers()).thenReturn((1..15).toList())

        presenter.search("13")

        verify(allEpisodesView).showEpisodes(listOf(13, 3, 1))

        verify(allEpisodesView, never()).showRefreshProgress(true)
        verify(allEpisodesView, never()).showRefreshProgress(false)
    }

    @Test
    fun pressStopSearchButton() {
        presenter.pressStopSearchButton()

        verify(allEpisodesView).showSortSpinner(true)
        verify(allEpisodesView).enableRefreshLayout(true)

        verify(allEpisodesView, never()).showSortSpinner(false)
        verify(allEpisodesView, never()).enableRefreshLayout(false)
    }

    @Test
    fun onEpisodeClicked() {
        val episodeNumber = 1
        presenter.onEpisodeClicked(episodeNumber)
        verify(router).navigateTo(Screens.EPISODE_SCREEN, episodeNumber)
    }

    @Test
    fun onAscPressed() {
        val episodeNumbers = (1..12).toList()
        whenever(allEpisodesPresenterCache.getEpisodeNumbers()).thenReturn(episodeNumbers)

        presenter.onAscPressed()

        verify(allEpisodesView).showEpisodes(episodeNumbers)
    }

    @Test
    fun onDescPressed() {
        val episodeNumbers = (1..12).toList()
        whenever(allEpisodesPresenterCache.getEpisodeNumbers()).thenReturn(episodeNumbers)

        presenter.onDescPressed()

        verify(allEpisodesView).showEpisodes(episodeNumbers.reversed())
    }
}