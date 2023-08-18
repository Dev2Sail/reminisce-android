package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class WithdrawDialog(activity: Activity) {
    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.setText(R.string.dialog_withdraw_title)
        viewBinding.dialogHomeCategoryDeleteBody.setText(R.string.dialog_withdraw_body)
        viewBinding.dialogHomeCategoryDeleteRemove.setText(R.string.dialog_withdraw_ok)
        viewBinding.dialogHomeCategoryDeleteCancel.setText(R.string.dialog_cancel)

        dialog.show()

        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener {
            dialog.dismiss()
        }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            // launcher로 이동시켜야 함...!
            activity.finish()
        }
    }
}