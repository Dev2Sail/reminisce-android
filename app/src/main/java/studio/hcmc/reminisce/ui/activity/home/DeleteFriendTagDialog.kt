package studio.hcmc.reminisce.ui.activity.home

import android.content.Context
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class DeleteFriendTagDialog(
    context: Context,
    opponentId: Int,
    friendIdx: Int,
    position: Int,
    delegate: Delegate
) {
    interface Delegate {
        fun onDeleteClick(opponentId: Int, friendIdx: Int, position: Int)
    }

    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = context.getString(R.string.dialog_delete_friend_title)
        viewBinding.dialogHomeCategoryDeleteBody.text = context.getString(R.string.dialog_delete_friend_body)
        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            delegate.onDeleteClick(opponentId, friendIdx, position)
        }
        dialog.show()
    }
}