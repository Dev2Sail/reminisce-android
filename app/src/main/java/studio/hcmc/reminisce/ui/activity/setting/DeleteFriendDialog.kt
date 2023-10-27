package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class DeleteFriendDialog(
    activity: Activity,
    opponentId: Int,
    position: Int,
    delegate: Delegate
) {
    interface Delegate {
        fun onDeleteClick(opponentId: Int, position: Int)
    }
    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = activity.getText(R.string.dialog_delete_friend_title)
        viewBinding.dialogHomeCategoryDeleteBody.text = activity.getText(R.string.dialog_delete_friend_body)
        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            delegate.onDeleteClick(opponentId, position)
            dialog.dismiss()
        }

        dialog.show()
    }
}