package pro.vylgin.radiot.presentation.news

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.getTransitionNames
import pro.vylgin.radiot.extension.humanTime
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class NewsPresenter @Inject constructor(
        private val news: Entry,
        private val router: Router
) : MvpPresenter<NewsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showNews()
    }

    private fun showNews() {
        viewState.run {
            news.apply {
                showToolbarTitle(title)
                val (titleTransitionName, dateTransitionName) = getTransitionNames()
                showNewsInfo(news.title, news.date.humanTime(), titleTransitionName, dateTransitionName)
            }
        }
    }

    fun onMenuClick() = onBackPressed()
    fun onBackPressed() = router.exit()

    fun transitionAnimationEnd() {
        showNewsShowNotes()
    }

    private fun showNewsShowNotes() {
        viewState.showNewsShowNotes(news.showNotes ?: "")
    }

}

