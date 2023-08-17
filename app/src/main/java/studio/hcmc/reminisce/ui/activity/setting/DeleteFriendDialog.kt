package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class DeleteFriendDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onDoneClick()
    }

    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = R.string.delete_friend_dialog_title.toString()
        viewBinding.dialogHomeCategoryDeleteBody.text = R.string.delete_friend_dialog_body.toString()

        dialog.show()

        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener {
            dialog.dismiss()
        }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            delegate.onDoneClick()
        }
    }
}
