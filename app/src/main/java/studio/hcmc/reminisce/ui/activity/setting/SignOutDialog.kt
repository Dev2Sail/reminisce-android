package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class SignOutDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onDoneClick()
    }
    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = activity.getText(R.string.setting_signout)
        viewBinding.dialogHomeCategoryDeleteBody.text = activity.getText(R.string.setting_sign_out_body)
        viewBinding.dialogHomeCategoryDeleteRemove.text = activity.getText(R.string.dialog_stop_writing_ok)
        viewBinding.dialogHomeCategoryDeleteCancel.text = activity.getText(R.string.dialog_stop_writing_cancel)
        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            delegate.onDoneClick()
        }
        dialog.show()
    }
}