package app.solocoin.solocoin.util

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import app.solocoin.solocoin.R
import app.solocoin.solocoin.util.enums.DialogType

/**
 * Created by Aditya Sonel on 27/04/20.
 */

class AppDialog: DialogFragment() {
    companion object {
        private const val TYPE = "TYPE"
        private const val TITLE = "TITLE"
        private const val SUBTITLE = "SUBTITLE"
        private const val CONFIRM_TITLE = "CONFIRM_TITLE"
        private const val CANCEL_TITLE = "CANCEL_TITLE"

        /**
         * instance method for success, error or info type dialog
         */
        fun instance(title: String, subtitle: String, listener: AppDialogListener?,  confirmTitle: String = "Okay", cancelTitle: String = "Cancel") = AppDialog().apply {
            val bundle = Bundle()
            bundle.putSerializable(TYPE, DialogType.INFO)
            bundle.putString(TITLE, title)
            bundle.putString(SUBTITLE, subtitle)
            bundle.putString(CONFIRM_TITLE, confirmTitle)
            bundle.putString(CANCEL_TITLE, cancelTitle)
            arguments = bundle

            dialogListener = listener
        }

        /**
         * instance method for loading type dialog
         */
        fun instance() = AppDialog().apply {
            val bundle = Bundle()
            bundle.putSerializable(TYPE, DialogType.LOADING)
            arguments = bundle
        }
    }

    private var type = DialogType.LOADING
    private var title: String ?= null
    private var subtitle: String ?= null
    private var confirmtitle: String ?= null
    private var canceltitle: String ?= null

    private var dialogListener: AppDialogListener ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false

        if (arguments != null) {
            type = arguments?.getSerializable(TYPE) as DialogType
            title = arguments?.getString(TITLE)
            subtitle = arguments?.getString(SUBTITLE)

            confirmtitle = arguments?.getString(CONFIRM_TITLE)
            canceltitle = arguments?.getString(CANCEL_TITLE)
        }

        return if (type == DialogType.LOADING) {
            inflater.inflate(R.layout.fragment_app_dialog_loading, container, false)
        } else {
            inflater.inflate(R.layout.fragment_app_dialog_snf, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (type == DialogType.INFO) {
            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            val tvSubTitle = view.findViewById<TextView>(R.id.tv_subtitle)
            val tvConfirm = view.findViewById<TextView>(R.id.tv_confirm)
            val tvCancel = view.findViewById<TextView>(R.id.tv_cancel)

            tvTitle.text = title
            tvSubTitle.text = subtitle
            tvConfirm.text = confirmtitle
            tvCancel.text = canceltitle

            tvConfirm.setOnClickListener {
                dialogListener?.onClickConfirm()
                dismiss()
            }
            tvCancel.setOnClickListener {
                dialogListener?.onClickCancel()
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppDialogListener) {
            dialogListener = context
        }
    }

    interface AppDialogListener {
        fun onClickConfirm()
        fun onClickCancel()
    }
}