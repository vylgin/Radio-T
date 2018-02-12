package pro.vylgin.radiot.ui.drawer

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
import toothpick.Toothpick


class NavigationDrawerFragment : BaseFragment(), NavigationDrawerView {
    override val layoutRes = R.layout.fragment_nav_drawer

    private val itemClickListener = { view: View ->
        presenter.onMenuItemClick(view.tag as NavigationDrawerView.MenuItem)
    }

    @InjectPresenter lateinit var presenter: NavigationDrawerPresenter

    @ProvidePresenter
    fun providePresenter(): NavigationDrawerPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(NavigationDrawerPresenter::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lastEntriesMI.tag = MenuItem.LAST_ENTRIES
        lastEntriesMI.setOnClickListener(itemClickListener)

        allEpisodesMI.tag = MenuItem.ALL_EPISODES
        allEpisodesMI.setOnClickListener(itemClickListener)
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