package pro.vylgin.radiot.presentation.launch.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.presentation.launch.LaunchContract

@StateStrategyType(OneExecutionStateStrategy::class)
interface LaunchView : MvpView, LaunchContract.View {

}
