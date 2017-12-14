package pro.vylgin.radiot.ui.lastentries

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_last_entries.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.presentation.lastentries.LastEntriesPresenter
import pro.vylgin.radiot.presentation.lastentries.LastEntriesView
import pro.vylgin.radiot.toothpick.DI
import pro.vylgin.radiot.ui.global.BaseFragment
import toothpick.Toothpick


class LastEntriesFragment : BaseFragment(), LastEntriesView {

    override val layoutRes = R.layout.fragment_last_entries

    @InjectPresenter lateinit var presenter: LastEntriesPresenter

    @ProvidePresenter
    fun providePresenter(): LastEntriesPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(LastEntriesPresenter::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationOnClickListener { presenter.onMenuClick() }
    }

    override fun showEntries(entries: List<Entry>) {
        showSnackMessage("entries size = " + entries.size)
    }

    override fun showError(message: String) {
        showSnackMessage(message)
    }

    override fun onBackPressed() = presenter.onBackPressed()
}