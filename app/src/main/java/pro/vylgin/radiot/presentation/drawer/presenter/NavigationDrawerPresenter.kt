package pro.vylgin.radiot.presentation.drawer.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import pro.vylgin.radiot.Screens
import pro.vylgin.radiot.presentation.drawer.NavigationDrawerContract
import pro.vylgin.radiot.presentation.drawer.view.NavigationDrawerView
import pro.vylgin.radiot.presentation.drawer.view.NavigationDrawerView.MenuItem
import pro.vylgin.radiot.presentation.drawer.view.NavigationDrawerView.MenuItem.ALL_EPISODES
import pro.vylgin.radiot.presentation.drawer.view.NavigationDrawerView.MenuItem.LAST_ENTRIES
import pro.vylgin.radiot.presentation.global.presenter.GlobalMenuController
import ru.terrakok.cicerone.Router
import javax.inject.Inject


@InjectViewState
class NavigationDrawerPresenter @Inject constructor(
        private val router: Router,
        private val menuController: GlobalMenuController
) : MvpPresenter<NavigationDrawerView>(), NavigationDrawerContract.Presenter {

    private var currentSelectedItem: NavigationDrawerView.MenuItem? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
    }

    override fun onScreenChanged(item: NavigationDrawerView.MenuItem) {
        menuController.close()
        currentSelectedItem = item
        viewState.selectMenuItem(item)
    }

    override fun onMenuItemClick(item: MenuItem) {
        menuController.close()
        if (item != currentSelectedItem) {
            when (item) {
                LAST_ENTRIES -> router.newRootScreen(Screens.LAST_ENTRIES_SCREEN)
                ALL_EPISODES -> router.newRootScreen(Screens.ALL_EPISODES_SCREEN)
            }
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}