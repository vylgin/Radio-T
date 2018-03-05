package pro.vylgin.radiot.presentation.news.view

import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionListenerAdapter
import android.transition.TransitionManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_news.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.presentation.global.view.BaseFragment
import pro.vylgin.radiot.presentation.news.presenter.NewsPresenter
import pro.vylgin.radiot.toothpick.DI
import toothpick.Toothpick
import toothpick.config.Module


class NewsFragment : BaseFragment(), NewsView {

    companion object {
        private const val ARG_NEWS = "arg_news"

        fun createNewInstance(news: Entry) = NewsFragment().apply {
            arguments = Bundle().also {
                it.putParcelable(ARG_NEWS, news)
            }
        }
    }

    override val layoutRes = R.layout.fragment_news

    private val transitionListener = object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) = presenter.showNewsShowNotes()
    }

    @InjectPresenter lateinit var presenter: NewsPresenter

    @ProvidePresenter
    fun providePresenter(): NewsPresenter {
        val scopeName = "news scope"
        val scope = Toothpick.openScopes(DI.MAIN_ACTIVITY_SCOPE, scopeName)
        scope.installModules(object : Module() {
            init {
                val news = arguments?.getParcelable<Entry>(ARG_NEWS)
                bind(Entry::class.java)
                        .toInstance(news)
            }
        })
        return scope.getInstance(NewsPresenter::class.java).also {
            Toothpick.closeScope(scopeName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context)
                    .inflateTransition(android.R.transition.move)
                    .addListener(transitionListener)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationOnClickListener { presenter.onMenuClick() }
    }

    override fun showMessage(message: String) {
        showSnackMessage(message)
    }

    override fun showToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun showNewsInfo(title: String, date: String, titleTransitionName: String, dateTransitionName: String) {
        if (titleTransitionName.isNotEmpty() &&
                dateTransitionName.isNotEmpty() &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTV.transitionName = titleTransitionName
            dateTV.transitionName = dateTransitionName
        }

        titleTV.text = title
        dateTV.text = date
    }

    override fun showNewsShowNotes(showNotes: String) {
        timeLabelsLL.visibility = View.GONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(infoLL)
        }
        showNotesTV.visibility = View.VISIBLE
        showNotesTV.text = showNotes
    }

    override fun showProgress(show: Boolean) {
        showProgressDialog(show)
    }

    override fun onBackPressed() = presenter.onBackPressed()

}