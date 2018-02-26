package pro.vylgin.radiot.presentation.launch.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import pro.vylgin.radiot.presentation.launch.LaunchContract
import pro.vylgin.radiot.presentation.launch.view.LaunchView
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class LaunchPresenter @Inject constructor(
        private val router: Router
) : MvpPresenter<LaunchView>(), LaunchContract.Presenter {

    override fun onFirstViewAttach() {
        viewState.initMainScreen()
    }

    override fun onBackPressed() = router.finishChain()

}
