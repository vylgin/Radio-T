package pro.vylgin.radiot.presentation.player.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.presentation.player.PlayerContract


@StateStrategyType(AddToEndSingleStrategy::class)
interface PlayerView : MvpView, PlayerContract.View {

}