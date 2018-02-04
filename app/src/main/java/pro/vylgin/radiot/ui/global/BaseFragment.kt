package pro.vylgin.radiot.ui.global

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatFragment
import pro.vylgin.radiot.R
import pro.vylgin.radiot.extension.color
import pro.vylgin.radiot.ui.episode.EpisodeFragment


abstract class BaseFragment : MvpAppCompatFragment() {
    companion object {
        private val PROGRESS_TAG = "bf_progress"
    }

    abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this is EpisodeFragment) {
                activity?.window?.statusBarColor = resources.color(R.color.transparent)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(layoutRes, container, false)

    protected fun showProgressDialog(progress: Boolean) {
        val fragment = childFragmentManager.findFragmentByTag(PROGRESS_TAG)
        if (fragment != null && !progress) {
            (fragment as ProgressDialog).dismissAllowingStateLoss()
            childFragmentManager.executePendingTransactions()
        } else if (fragment == null && progress) {
            ProgressDialog().show(childFragmentManager, PROGRESS_TAG)
            childFragmentManager.executePendingTransactions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this is EpisodeFragment) {
                activity?.window?.statusBarColor = resources.color(R.color.colorPrimaryDark)
            }
        }
    }

    protected fun showSnackMessage(message: String) {
        view?.let {
            val snackbar = Snackbar.make(it, message, Snackbar.LENGTH_LONG)
            val messageTextView = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
            messageTextView.setTextColor(Color.WHITE)
            snackbar.show()
        }
    }

    open fun onBackPressed() {}
}