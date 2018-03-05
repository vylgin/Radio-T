package pro.vylgin.radiot.presentation.drawer.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.presentation.drawer.NavigationDrawerContract


@StateStrategyType(AddToEndSingleStrategy::class)
interface NavigationDrawerView : MvpView, NavigationDrawerContract.View {
    enum class MenuItem {
        LAST_ENTRIES,
        ALL_EPISODES
    }

    override fun selectMenuItem(item: MenuItem)
}