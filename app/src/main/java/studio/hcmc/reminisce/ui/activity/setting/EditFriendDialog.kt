package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.databinding.DialogEditFriendBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.vo.user.UserVO

class EditFriendDialog(
    activity: Activity,
    opponentId: Int,
    savedNickname: String,
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
        // friend로 등록된 user의 nickname
        viewBinding.dialogEditFriendFieldEditText.hint = delegate.getUser(opponentId).nickname
        // friend로 등록된 user의 email
        viewBinding.dialogEditFriendField.helperText = delegate.getUser(opponentId).email
        // friend에 등록된 nickname
        viewBinding.dialogEditFriendTitle.text = savedNickname

        viewBinding.apply {
            dialogEditFriendField.editText!!.addTextChangedListener {
                if (dialogEditFriendField.string.length > 20) {
                    dialogEditFriendSave.isEnabled = false
                }
            }
        }



//        viewBinding.apply {
//            dialogEditFriendField.editText!!.addTextChangedListener {
////                dialogEditFriendSave.isEnabled.apply {
//////                    dialogEditFriendField.string.isNotEmpty() && dialogEditFriendField.string.length <= 20
////                    dialogEditFriendField.string.length <= 20
////                }
////                dialogEditFriendSave.isEnabled = dialogEditFriendField.string.isNotEmpty() && dialogEditFriendField.string.length <= 20
//            }
//        }
        viewBinding.dialogEditFriendSave.setOnClickListener {
            val input = viewBinding.dialogEditFriendField.string
            when {
                // friend nickname 삭제
                input.isEmpty() -> {
                    delegate.onEditClick(opponentId, null, position)
                    dialog.dismiss()
                }
                input.isNotEmpty() && input.length <= 20 -> {
                    delegate.onEditClick(opponentId, viewBinding.dialogEditFriendField.string, position)
                    dialog.dismiss()
                }
            }
        }
        viewBinding.dialogEditFriendCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}