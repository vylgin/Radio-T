package pro.vylgin.radiot.ui.global

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import pro.vylgin.radiot.R


class ConfirmDialog : DialogFragment() {
    private var clickListener: OnClickListener? = null

    private val title: String? get() = arguments?.getString(TITLE)
    private val msg: String get() = arguments?.getString(MSG) ?: ""
    private val positive: String get() = arguments?.getString(POSITIVE_TEXT) ?: getString(R.string.ok)
    private val negative: String get() = arguments?.getString(NEGATIVE_TEXT) ?: getString(R.string.cancel)
    private val dialogTag: String get() = arguments?.getString(TAG) ?: "ConfirmDialog tag"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(context!!, R.style.AppCompatAlertDialogStyle).apply {
                title?.let { setTitle(title) }
                setMessage(msg)
                setPositiveButton(positive) { _, _ -> clickListener?.dialogConfirm(dialogTag) }
                setNegativeButton(negative) { _, _ -> dismiss() }
            }.create()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        clickListener = when {
            parentFragment is OnClickListener -> parentFragment as OnClickListener
            activity is OnClickListener -> activity as OnClickListener
            else -> null
        }
    }

    override fun onDetach() {
        clickListener = null
        super.onDetach()
    }

    companion object {
        private const val TITLE = "title"
        private const val MSG = "msg"
        private const val POSITIVE_TEXT = "positive_text"
        private const val NEGATIVE_TEXT = "negative_text"
        private const val TAG = "tag"

        fun newInstants(
                title: String? = null,
                msg: String,
                positive: String? = null,
                negative: String? = null,
                tag: String
        ) =
                ConfirmDialog().apply {
                    arguments = Bundle().apply {
                        putString(TITLE, title)
                        putString(MSG, msg)
                        putString(POSITIVE_TEXT, positive)
                        putString(NEGATIVE_TEXT, negative)
                        putString(TAG, tag)
                    }
                }
    }

    interface OnClickListener {
        fun dialogConfirm(tag: String)
    }
}