package pro.vylgin.radiot.presentation.lastentries

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.presentation.allepisodes.AllEpisodesContract


@StateStrategyType(AddToEndSingleStrategy::class)
interface AllEpisodesView : MvpView, AllEpisodesContract.View{

}