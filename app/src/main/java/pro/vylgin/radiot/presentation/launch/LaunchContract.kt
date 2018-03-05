package pro.vylgin.radiot.presentation.launch

interface LaunchContract {

    interface View {
        fun initMainScreen()
    }

    interface Presenter {
        fun onBackPressed()
    }

}