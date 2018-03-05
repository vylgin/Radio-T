package pro.vylgin.radiot.presentation.news.presenter

import com.arellomobile.mvp.InjectViewState
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.getTransitionNames
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.presentation.global.presenter.BasePresenter
import pro.vylgin.radiot.presentation.news.NewsContract
import pro.vylgin.radiot.presentation.news.view.NewsView
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class NewsPresenter @Inject constructor(
        private val news: Entry,
        private val router: Router
) : BasePresenter<NewsView>(), NewsContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showNews()
    }

    override fun showNews() {
        viewState.run {
            news.apply {
                showToolbarTitle(title)
                val (titleTransitionName, dateTransitionName) = getTransitionNames()
                showNewsInfo(news.title, news.date.humanTime(), titleTransitionName, dateTransitionName)
            }
        }
    }

    override fun onMenuClick() = onBackPressed()
    override fun onBackPressed() = router.exit()

    override fun showNewsShowNotes() {
        viewState.showNewsShowNotes(news.showNotes ?: "")
    }

}


