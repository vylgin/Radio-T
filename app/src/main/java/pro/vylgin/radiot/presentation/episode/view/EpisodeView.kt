package pro.vylgin.radiot.presentation.episode.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.presentation.episode.EpisodeContract


@StateStrategyType(AddToEndSingleStrategy::class)
interface EpisodeView : MvpView, EpisodeContract.View {

    @StateStrategyType(OneExecutionStateStrategy::class)
    override fun showMessage(message: String)

}