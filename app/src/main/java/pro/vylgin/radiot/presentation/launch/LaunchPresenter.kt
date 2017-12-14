package pro.vylgin.radiot.presentation.launch

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class LaunchPresenter @Inject constructor(
        private val router: Router
) : MvpPresenter<LaunchView>() {

    override fun onFirstViewAttach() {
        viewState.initMainScreen()
    }

    fun onBackPressed() = router.finishChain()

}
