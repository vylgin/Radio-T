package pro.vylgin.radiot.ui.launch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_launch.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.Screens
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.presentation.drawer.NavigationDrawerView
import pro.vylgin.radiot.presentation.global.GlobalMenuController
import pro.vylgin.radiot.presentation.launch.LaunchPresenter
import pro.vylgin.radiot.presentation.launch.LaunchView
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.toothpick.module.MainActivityModule
import pro.vylgin.radiot.ui.drawer.NavigationDrawerFragment
import pro.vylgin.radiot.ui.global.BaseActivity
import pro.vylgin.radiot.ui.global.BaseFragment
import pro.vylgin.radiot.ui.global.list.EntrySharedElement
import pro.vylgin.radiot.ui.lastentries.LastEntriesFragment
import pro.vylgin.radiot.ui.podcast.PodcastFragment
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace
import toothpick.Toothpick
import javax.inject.Inject

class LaunchActivity : BaseActivity(), LaunchView {

    @Inject lateinit var navigationHolder: NavigatorHolder
    @Inject lateinit var menuController: GlobalMenuController

    private var menuStateDisposable: Disposable? = null

    override val layoutRes = R.layout.activity_launch

    private val currentFragment
        get() = supportFragmentManager.findFragmentById(R.id.mainContainer) as BaseFragment?

    private val drawerFragment
        get() = supportFragmentManager.findFragmentById(R.id.navDrawerContainer) as NavigationDrawerFragment?

    @InjectPresenter lateinit var presenter: LaunchPresenter

    @ProvidePresenter
    fun providePresenter(): LaunchPresenter {
        return Toothpick
                .openScope(DI.SERVER_SCOPE)
                .getInstance(LaunchPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        Toothpick.openScopes(DI.SERVER_SCOPE, DI.MAIN_ACTIVITY_SCOPE).apply {
            installModules(MainActivityModule())
            Toothpick.inject(this@LaunchActivity, this)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        menuStateDisposable = menuController.state.subscribe { openNavDrawer(it) }
        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        menuStateDisposable?.dispose()
        navigationHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) Toothpick.closeScope(DI.MAIN_ACTIVITY_SCOPE)
    }

    override fun initMainScreen() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainContainer, LastEntriesFragment())
                .replace(R.id.navDrawerContainer, NavigationDrawerFragment())
                .commitNow()
        updateNavDrawer()
    }

    private val navigator = object : SupportAppNavigator(this, R.id.mainContainer) {

        override fun applyCommand(command: Command?) {
            super.applyCommand(command)
            updateNavDrawer()
        }

        override fun createActivityIntent(screenKey: String?, data: Any?): Intent?  = when (screenKey) {
            Screens.PREP_SCREEN -> Intent(Intent.ACTION_VIEW, Uri.parse((data as Entry).url))
            else -> null
        }

        override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
            Screens.LAST_ENTRIES_SCREEN -> LastEntriesFragment()
            Screens.PODCAST_SCREEN -> PodcastFragment.createNewInstance((data as EntrySharedElement).entry)
//            Screens.ALL_PODCASTS_SCREEN -> AllPodcastsFragment()
//            Screens.SEARCH_SCREEN -> SearchFragment.createNewInstance()
            else -> null
        }

        override fun setupFragmentTransactionAnimation(command: Command?, currentFragment: Fragment?,
                                                       nextFragment: Fragment?, fragmentTransaction: FragmentTransaction?) {
            super.setupFragmentTransactionAnimation(command, currentFragment, nextFragment, fragmentTransaction)

            if (currentFragment is LastEntriesFragment && nextFragment is PodcastFragment) {
                val entrySharedElement: EntrySharedElement = (command as? Replace)?.transitionData as? EntrySharedElement ?:
                        (command as Forward).transitionData as EntrySharedElement

                fragmentTransaction?.addSharedElement(entrySharedElement.sharedImageView, ViewCompat.getTransitionName(entrySharedElement.sharedImageView))
                fragmentTransaction?.addSharedElement(entrySharedElement.titleSharedTextView, ViewCompat.getTransitionName(entrySharedElement.titleSharedTextView))
                fragmentTransaction?.addSharedElement(entrySharedElement.dateSharedTextView, ViewCompat.getTransitionName(entrySharedElement.dateSharedTextView))
            }
        }
    }

    //region nav drawer
    private fun openNavDrawer(open: Boolean) {
        drawerLayout.postDelayed({
            if (open) drawerLayout.openDrawer(GravityCompat.START)
            else drawerLayout.closeDrawer(GravityCompat.START)
        }, 150)
    }

    private fun enableNavDrawer(enable: Boolean) {
        drawerLayout.setDrawerLockMode(
                if (enable) DrawerLayout.LOCK_MODE_UNLOCKED
                else DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                GravityCompat.START
        )
    }

    private fun updateNavDrawer() {
        supportFragmentManager.executePendingTransactions()

        drawerFragment?.let { drawerFragment ->
            currentFragment?.let {
                when (it) {
                    is LastEntriesFragment -> drawerFragment.onScreenChanged(NavigationDrawerView.MenuItem.LAST_ENTRIES)
//                    is AllPodcastsFragment -> drawerFragment.onScreenChanged(NavigationDrawerView.MenuItem.ALL_PODCASTS)
//                    is SearchFragment -> drawerFragment.onScreenChanged(NavigationDrawerView.MenuItem.SEARCH)
                }
                enableNavDrawer(isNavDrawerAvailableForFragment(it))
            }
        }
    }

    private fun isNavDrawerAvailableForFragment(currentFragment: Fragment) = when (currentFragment) {
        is LastEntriesFragment -> true
//        is AllPodcastsFragment -> true
//        is SearchFragment -> true
        else -> false
    }
    //endregion

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            openNavDrawer(false)
        } else {
            currentFragment?.onBackPressed() ?: presenter.onBackPressed()
        }
    }
}
