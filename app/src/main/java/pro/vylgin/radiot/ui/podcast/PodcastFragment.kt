package pro.vylgin.radiot.ui.podcast

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionListenerAdapter
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_podcast.*
import kotlinx.android.synthetic.main.item_time_label.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.extension.humanDuration
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.loadImage
import pro.vylgin.radiot.presentation.podcast.PodcastPresenter
import pro.vylgin.radiot.presentation.podcast.PodcastView
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.toothpick.PrimitiveWrapper
import pro.vylgin.radiot.toothpick.qualifier.PodcastNumber
import pro.vylgin.radiot.ui.global.BaseFragment
import toothpick.Toothpick
import toothpick.config.Module
import java.util.*


class PodcastFragment : BaseFragment(), PodcastView {

    companion object {
        private const val ARG_PODCAST = "arg_podcast"
        private const val ARG_PODCAST_NUMBER = "arg_podcast_number"

        fun createNewInstance(podcast: Entry) = PodcastFragment().apply {
            arguments = Bundle().also {
                it.putParcelable(ARG_PODCAST, podcast)
            }
        }

        fun createNewInstance(podcastNumber: Int) = PodcastFragment().apply {
            arguments = Bundle().also {
                it.putInt(ARG_PODCAST_NUMBER, podcastNumber)
            }
        }
    }

    override val layoutRes = R.layout.fragment_podcast

    private val transitionListener = object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) = presenter.transitionAnimationEnd()
    }

    @InjectPresenter lateinit var presenter: PodcastPresenter

    @ProvidePresenter
    fun providePresenter(): PodcastPresenter {
        val scopeName = "podcast scope"
        val scope = Toothpick.openScopes(DI.MAIN_ACTIVITY_SCOPE, scopeName)
        scope.installModules(object : Module() {
            init {
                var podcast = arguments?.getParcelable<Entry>(ARG_PODCAST)
                if (podcast == null) {
                    podcast = Entry("", "", Date(), listOf(), null, null, null, null, null)
                }
                bind(Entry::class.java)
                        .toInstance(podcast)
                bind(PrimitiveWrapper::class.java)
                        .withName(PodcastNumber::class.java)
                        .toInstance(PrimitiveWrapper(arguments?.getInt(ARG_PODCAST_NUMBER)))
            }
        })
        return scope.getInstance(PodcastPresenter::class.java).also {
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

    override fun showToolbarImage(imageUrl: String?, transitionName: String) {
        if (transitionName.isNotEmpty() &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            posterIV.transitionName = transitionName
        }

        posterIV.loadImage(imageUrl, activity)
    }

    override fun showPodcastInfo(podcast: Entry, titleTransitionName: String, dateTransitionName: String) {
        if (titleTransitionName.isNotEmpty() &&
                dateTransitionName.isNotEmpty() &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTV.transitionName = titleTransitionName
            dateTV.transitionName = dateTransitionName
        }

        titleTV.text = podcast.title
        dateTV.text = podcast.date.humanTime(resources)
    }

    override fun showPodcastShowNotes(showNotes: String) {
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
                showMessage("Воспроизвести \"${timeLabel.topic}\"")
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