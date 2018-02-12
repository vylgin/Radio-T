package pro.vylgin.radiot.presentation.drawer

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndSingleStrategy::class)
interface NavigationDrawerView : MvpView {
    enum class MenuItem {
        LAST_ENTRIES,
        ALL_EPISODES
    }

    fun selectMenuItem(item: MenuItem)
}