package pro.vylgin.radiot.presentation.episode

import pro.vylgin.radiot.entity.TimeLabel

interface EpisodeContract {

    interface View {
        fun showMessage(message: String)
        fun showProgress(show: Boolean)
        fun showToolbarTitle(title: String)
        fun showToolbarImage(imageUrl: String?, transitionName: String = "")
        fun showEpisodeInfo(title: String, date: String, titleTransitionName: String = "", dateTransitionName: String = "")
        fun showEpisodeShowNotes(showNotes: String)
        fun showTimeLabels(timeLabels: List<TimeLabel>)
    }

    interface Presenter {
        fun loadEpisode()
        fun onMenuClick()
        fun onBackPressed()
        fun showTimeLabelsOrShowNotes()
        fun playEpisode()
        fun seekTo(timeLabel: TimeLabel)
    }

}