package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class WithdrawDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onDoneClick()
    }
    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(activity)).apply {
            dialogHomeCategoryDeleteTitle.text = activity.getText(R.string.dialog_withdraw_title)
            dialogHomeCategoryDeleteBody.text = activity.getText(R.string.dialog_withdraw_body)
            dialogHomeCategoryDeleteRemove.text = activity.getText(R.string.dialog_withdraw)
        }
        val dialog = BottomSheetDialog(activity, viewBinding)

        dialog.show()
        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            delegate.onDoneClick()
        }
    }
}