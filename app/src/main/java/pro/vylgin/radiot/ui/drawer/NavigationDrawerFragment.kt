package pro.vylgin.radiot.ui.drawer

import android.content.Context
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_nav_drawer.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.presentation.drawer.NavigationDrawerPresenter
import pro.vylgin.radiot.presentation.drawer.NavigationDrawerView
import pro.vylgin.radiot.presentation.drawer.NavigationDrawerView.MenuItem
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.ui.global.BaseFragment
import pro.vylgin.radiot.ui.launch.LaunchActivity
import toothpick.Toothpick


class NavigationDrawerFragment : BaseFragment(), NavigationDrawerView {
    override val layoutRes = R.layout.fragment_nav_drawer
    private var launchActivity: LaunchActivity? = null

    private val itemClickListener = { view: View ->
        presenter.onMenuItemClick(view.tag as MenuItem)
    }

    @InjectPresenter lateinit var presenter: NavigationDrawerPresenter

    @ProvidePresenter
    fun providePresenter(): NavigationDrawerPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(NavigationDrawerPresenter::class.java)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        launchActivity = activity as LaunchActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lastEntriesMI.tag = MenuItem.LAST_ENTRIES
        allPodcastsMI.tag = MenuItem.ALL_PODCASTS
        searchMI.tag = MenuItem.SEARCH

        lastEntriesMI.setOnClickListener(itemClickListener)
        allPodcastsMI.setOnClickListener(itemClickListener)
        searchMI.setOnClickListener(itemClickListener)
    }

    override fun selectMenuItem(item: MenuItem) {
        (0 until navDrawerMenuContainer.childCount)
                .map { navDrawerMenuContainer.getChildAt(it) }
                .forEach { menuItem -> menuItem.tag?.let { menuItem.isSelected = it == item } }
    }

    fun onScreenChanged(item: MenuItem) {
        presenter.onScreenChanged(item)
    }

}