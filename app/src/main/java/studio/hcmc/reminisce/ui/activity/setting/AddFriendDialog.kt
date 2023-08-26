package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
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
        val builder = StringBuilder()
        builder.append(nickname)
        builder.append("님을 친구로 등록할까요?")
        viewBinding.dialogAddFriendBody.text = builder.toString()

        dialog.show()

        viewBinding.dialogAddFriendAdd.setOnClickListener {
            dialog.dismiss()
            delegate.onAddClick(opponentId)

        }
        viewBinding.dialogAddFriendCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
}