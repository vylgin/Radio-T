package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import pro.vylgin.radiot.extension.addTo
import pro.vylgin.radiot.model.interactor.entries.EntriesInteractor
import pro.vylgin.radiot.presentation.global.GlobalMenuController
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class LastEntriesPresenter @Inject constructor(
        private val router: Router,
        private val entriesInteractor: EntriesInteractor,
        private val menuController: GlobalMenuController
) : MvpPresenter<LastEntriesView>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadLastEntries()
    }

    private fun loadLastEntries() {
        entriesInteractor.getEntries()
                .subscribe(
                        { viewState.showEntries(it) },
                        { viewState.showError("Ошибка загрузки последних выпусков") }
                )
                .addTo(compositeDisposable)
    }

    fun onMenuClick() = menuController.open()
    fun onBackPressed() = router.exit()
}