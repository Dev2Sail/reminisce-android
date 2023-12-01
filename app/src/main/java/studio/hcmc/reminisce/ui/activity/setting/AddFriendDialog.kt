package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogAddFriendBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class AddFriendDialog(
    activity: Activity,
    opponentId: Int,
    nickname: String,
    delegate: Delegate
) {
    interface Delegate {
        fun onAddClick(opponentId: Int)
    }
    init {
        val viewBinding = DialogAddFriendBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogAddFriendBody.text = activity.getString(R.string.add_friend_question, nickname)
        viewBinding.dialogAddFriendCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogAddFriendAdd.setOnClickListener {
            dialog.dismiss()
            delegate.onAddClick(opponentId)
        }
        dialog.show()
    }
}