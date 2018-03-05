package pro.vylgin.radiot.presentation.news.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pro.vylgin.radiot.presentation.news.NewsContract


@StateStrategyType(AddToEndSingleStrategy::class)
interface NewsView : MvpView, NewsContract.View {

    @StateStrategyType(OneExecutionStateStrategy::class)
    override fun showMessage(message: String)

}