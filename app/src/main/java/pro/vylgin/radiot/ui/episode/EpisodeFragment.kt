package pro.vylgin.radiot.ui.episode

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.transition.Transition
import android.support.transition.TransitionInflater
import android.support.transition.TransitionListenerAdapter
import android.support.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_episode.*
import kotlinx.android.synthetic.main.item_time_label.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.extension.humanDuration
import pro.vylgin.radiot.extension.loadImage
import pro.vylgin.radiot.presentation.episode.EpisodePresenter
import pro.vylgin.radiot.presentation.episode.EpisodeView
import pro.vylgin.radiot.presentation.global.view.BaseFragment
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.toothpick.PrimitiveWrapper
import pro.vylgin.radiot.toothpick.qualifier.EpisodeNumber
import toothpick.Toothpick
import toothpick.config.Module
import java.util.*


class EpisodeFragment : BaseFragment(), EpisodeView {

    companion object {
        private const val ARG_EPISODE = "arg_episode"
        private const val ARG_EPISODE_NUMBER = "arg_episode_number"

        fun createNewInstance(episode: Entry) = EpisodeFragment().apply {
            arguments = Bundle().also {
                it.putParcelable(ARG_EPISODE, episode)
            }
        }

        fun createNewInstance(episodeNumber: Int) = EpisodeFragment().apply {
            arguments = Bundle().also {
                it.putInt(ARG_EPISODE_NUMBER, episodeNumber)
            }
        }
    }

    override val layoutRes = R.layout.fragment_episode

    private val transitionListener = object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) = presenter.transitionAnimationEnd()
    }

    @InjectPresenter lateinit var presenter: EpisodePresenter

    @ProvidePresenter
    fun providePresenter(): EpisodePresenter {
        val scopeName = "episode scope"
        val scope = Toothpick.openScopes(DI.MAIN_ACTIVITY_SCOPE, scopeName)
        scope.installModules(object : Module() {
            init {
                var episode = arguments?.getParcelable<Entry>(ARG_EPISODE)
                if (episode == null) {
                    episode = Entry("", "", Date(), listOf(), null, null, null, null, null)
                }
                bind(Entry::class.java)
                        .toInstance(episode)
                bind(PrimitiveWrapper::class.java)
                        .withName(EpisodeNumber::class.java)
                        .toInstance(PrimitiveWrapper(arguments?.getInt(ARG_EPISODE_NUMBER)))
            }
        })
        return scope.getInstance(EpisodePresenter::class.java).also {
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
        playButton.setOnClickListener { presenter.playEpisode() }
    }

    override fun showMessage(message: String) {
        showSnackMessage(message)
    }

    override fun showToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun showToolbarImage(imageUrl: String?, transitionName: String) {
        if (transitionName.isNotEmpty() &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            posterIV.transitionName = transitionName
        }

        posterIV.loadImage(imageUrl, activity)
    }

    override fun showEpisodeInfo(title: String, date: String, titleTransitionName: String, dateTransitionName: String) {
        if (titleTransitionName.isNotEmpty() &&
                dateTransitionName.isNotEmpty() &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTV.transitionName = titleTransitionName
            dateTV.transitionName = dateTransitionName
        }

        titleTV.text = title
        dateTV.text = date
    }

    override fun showEpisodeShowNotes(showNotes: String) {
        timeLabelsLL.visibility = View.GONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(infoLL)
        }
        showNotesTV.visibility = View.VISIBLE
        showNotesTV.text = showNotes
    }

    override fun showTimeLabels(timeLabels: List<TimeLabel>) {
        initLabelsLayout(timeLabels)
    }

    @SuppressLint("SetTextI18n")
    private fun initLabelsLayout(timeLabels: List<TimeLabel>) {
        showNotesTV.visibility = View.GONE

        for ((index, timeLabel) in timeLabels.withIndex()) {
            val view = LayoutInflater.from(activity).inflate(R.layout.item_time_label, timeLabelsLL, false)
            view.topicNumberTV.text = "${index + 1}"
            view.topicTV.text = timeLabel.topic
            view.durationTV.text = timeLabel.humanDuration()

            view.topicConstraintLayout.setOnClickListener {
                presenter.seekTo(timeLabel)
            }

            timeLabelsLL.addView(view)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(infoLL)
        }
        timeLabelsLL.visibility = View.VISIBLE
    }

    override fun showProgress(show: Boolean) {
        showProgressDialog(show)
    }

    override fun onBackPressed() = presenter.onBackPressed()

}