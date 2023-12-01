package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.databinding.DialogEditFriendBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.stringOrNull
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class EditFriendDialog(
    activity: Activity,
    friend: FriendVO,
    position: Int,
    delegate: Delegate
) {
    interface Delegate {
        fun getUser(userId: Int): UserVO
        fun onEditClick(opponentId: Int, body: String?, position: Int)
    }

    init {
        val viewBinding = DialogEditFriendBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        // friend로 등록된 nickname, null일 경우 user의 nickname
        viewBinding.dialogEditFriendFieldEditText.hint = friend.nickname
        // friend로 등록된 user의 email
        viewBinding.dialogEditFriendField.helperText = delegate.getUser(friend.opponentId).email
        viewBinding.dialogEditFriendField.editText!!.addTextChangedListener {
            if (viewBinding.dialogEditFriendField.string.length > 20) {
                viewBinding.dialogEditFriendSave.isEnabled = false
            }
            if (viewBinding.dialogEditFriendField.string.length <= 20) {
                viewBinding.dialogEditFriendSave.isEnabled = true
            }
        }
        viewBinding.dialogEditFriendSave.setOnClickListener {
            val input = viewBinding.dialogEditFriendField.stringOrNull
            delegate.onEditClick(friend.opponentId, input, position)
            dialog.dismiss()
        }
        viewBinding.dialogEditFriendCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}