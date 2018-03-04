package pro.vylgin.radiot.presentation.launch.presenter

import com.arellomobile.mvp.InjectViewState
import pro.vylgin.radiot.presentation.global.presenter.BasePresenter
import pro.vylgin.radiot.presentation.launch.LaunchContract
import pro.vylgin.radiot.presentation.launch.view.LaunchView
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class LaunchPresenter @Inject constructor(
        private val router: Router
) : BasePresenter<LaunchView>(), LaunchContract.Presenter {

    override fun onFirstViewAttach() {
        initMainScreen()
    }

    private fun initMainScreen() {
        viewState.initMainScreen()
    }

    override fun onBackPressed() = router.finishChain()

}
