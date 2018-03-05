package pro.vylgin.radiot.presentation.news

interface NewsContract {

    interface View {
        fun showMessage(message: String)
        fun showProgress(show: Boolean)
        fun showToolbarTitle(title: String)
        fun showNewsInfo(title: String, date: String, titleTransitionName: String = "", dateTransitionName: String = "")
        fun showNewsShowNotes(showNotes: String)
    }

    interface Presenter {
        fun showNews()
        fun onMenuClick()
        fun onBackPressed()
        fun showNewsShowNotes()
    }

}