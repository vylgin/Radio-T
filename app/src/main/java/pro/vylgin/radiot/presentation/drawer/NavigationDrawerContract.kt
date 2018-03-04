package pro.vylgin.radiot.presentation.drawer

import pro.vylgin.radiot.presentation.drawer.view.NavigationDrawerView

interface NavigationDrawerContract {

    interface View {
        fun selectMenuItem(item: NavigationDrawerView.MenuItem)
    }

    interface Presenter {
        fun onScreenChanged(item: NavigationDrawerView.MenuItem)
        fun onMenuItemClick(item: NavigationDrawerView.MenuItem)
        fun onDestroy()
    }

}