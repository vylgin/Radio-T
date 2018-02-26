package pro.vylgin.radiot.ui.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.item_time_label.view.*
import kotlinx.android.synthetic.main.player_bottom_sheet.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.TimeLabel
import pro.vylgin.radiot.extension.color
import pro.vylgin.radiot.extension.humanDuration
import pro.vylgin.radiot.extension.loadImage
import pro.vylgin.radiot.presentation.global.view.BaseFragment
import pro.vylgin.radiot.presentation.player.PlayerPresenter
import pro.vylgin.radiot.presentation.player.PlayerView
import pro.vylgin.radiot.toothpick.DI
import toothpick.Toothpick


class PlayerFragment : BaseFragment(), PlayerView {

    override val layoutRes = R.layout.fragment_player

    private val seekDelay: Long = 1000

    @InjectPresenter lateinit var presenter: PlayerPresenter

    @ProvidePresenter
    fun providePresenter(): PlayerPresenter {
        val scopeName = "player scope"
        val scope = Toothpick.openScopes(DI.MAIN_ACTIVITY_SCOPE, scopeName)
        return scope.getInstance(PlayerPresenter::class.java).also {
            Toothpick.closeScope(scopeName)
        }
    }

    val playClickListener: (View) -> Unit = {
        presenter.playEpisode()
    }

    val pauseClickListener: (View) -> Unit = {
        presenter.pauseEpisode()
    }

    val nextClickListener: (View) -> Unit = {
        presenter.playNextTopic()
    }

    val prevClickListener: (View) -> Unit = {
        presenter.playPrevTopic()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seekHandler.removeCallbacks(seekRunnable)
        seekHandler.post(seekRunnable)

        initSeekBar()

        playIB.setOnClickListener(playClickListener)
        playBottomIB.setOnClickListener(playClickListener)
        pauseIB.setOnClickListener(pauseClickListener)
        pauseBottomIB.setOnClickListener(pauseClickListener)

        nextIB.setOnClickListener(nextClickListener)
        prevIB.setOnClickListener(prevClickListener)
    }

    private fun initSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val currentTimeMillis = progress * 1000
                if (fromUser) {
                    val value = progress * (seekBar.width - 2 * seekBar.thumbOffset) / seekBar.max

                    val seconds = (currentTimeMillis / 1000) % 60
                    val minutes = (currentTimeMillis / (1000 * 60) % 60)
                    val hours = (currentTimeMillis / (1000 * 60 * 60) % 24)
                    val currentTime = String.format("%d:%02d:%02d", hours, minutes, seconds)

                    seekOverThumbTV.text = currentTime
                    seekOverThumbTV.x = seekBar.x + value.toFloat() + (seekBar.thumbOffset / 2).toFloat()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekOverThumbTV.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                presenter.seekTo(seekBar.progress * 1000L)
                seekOverThumbTV.visibility = View.GONE
            }
        })
    }

    private val seekHandler = Handler()
    private val seekRunnable = object : Runnable {
        override fun run() {
            presenter.checkSeek()
            seekHandler.postDelayed(this, seekDelay)
        }
    }

    override fun updateSeek(progress: Int, buffered: Int, currentTime: String) {
        currentPositionTV.text = currentTime

        seekBar.progress = progress
        seekBar.secondaryProgress = buffered
    }

    override fun updateTitle(title: String) {
        titleTV.text = title
    }

    override fun updateSmallImage(image: String?) {
        littleImageIV.loadImage(image)
    }

    override fun updateDuration(duration: String, durationSec: Int) {
        seekBar.max = durationSec
        durationEpisodeTV.text = duration
    }

    override fun showTimeLabels(timeLabels: List<TimeLabel>) {
        initLabelsLayout(timeLabels)
    }

    override fun showEpisodeShowNotes(showNotes: String) {
        timeLabelsLL.visibility = View.GONE
        showNotesTV.visibility = View.VISIBLE
        showNotesTV.text = showNotes
    }

    override fun highlightCurrentTimeLabel(timeLabelPosition: Int) {
        var view: View?
        for (i in 0..timeLabelsLL.childCount) {
            view = timeLabelsLL.getChildAt(i)
            if (view == null) {
                continue
            }
            if (i == timeLabelPosition) {
                view.bgView.setBackgroundColor(resources.color(R.color.list_item_pressed))
            } else {
                view.bgView.setBackgroundColor(resources.color(R.color.transparent))
            }
        }
    }

    override fun showTimeLabelTitle() {
        timeLabelTV.visibility = View.VISIBLE
    }

    override fun hideTimeLabelTitle() {
        timeLabelTV.visibility = View.GONE
    }

    override fun updateCurrentTimeLabelTitle(topic: String) {
        timeLabelTV.text = topic
    }

    override fun showPlayButton() {
        playIB.visibility = View.VISIBLE
        playBottomIB.visibility = View.VISIBLE
        pauseIB.visibility = View.INVISIBLE
        pauseBottomIB.visibility = View.INVISIBLE
    }

    override fun showPauseButton() {
        pauseIB.visibility = View.VISIBLE
        pauseBottomIB.visibility = View.VISIBLE
        playIB.visibility = View.INVISIBLE
        playBottomIB.visibility = View.INVISIBLE
    }

    override fun showPrevAndNextButtons() {
        nextIB.visibility = View.VISIBLE
        prevIB.visibility = View.VISIBLE
    }

    override fun hidePrevAndNextButtons() {
        nextIB.visibility = View.INVISIBLE
        prevIB.visibility = View.INVISIBLE
    }

    override fun showPlayerPanel() {
        playerBottomSheetCL.visibility = View.VISIBLE
    }

    override fun hidePlayerPanel() {
        playerBottomSheetCL.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun initLabelsLayout(timeLabels: List<TimeLabel>) {
        timeLabelsLL.removeAllViews()

        showNotesTV.visibility = View.GONE

        for ((index, timeLabel) in timeLabels.withIndex()) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_time_label, timeLabelsLL, false)
            view.topicNumberTV.text = "${index + 1}"
            view.topicTV.text = timeLabel.topic
            view.durationTV.text = timeLabel.humanDuration()

            view.topicConstraintLayout.setOnClickListener {
                presenter.seekTo(timeLabel)
            }

            timeLabelsLL.addView(view)
        }

        timeLabelsLL.visibility = View.VISIBLE
    }

    override fun onBackPressed() = presenter.onBackPressed()

    override fun onDetach() {
        super.onDetach()
        seekHandler.removeCallbacks(seekRunnable)
    }
}