package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.databinding.DialogEditFriendBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.stringOrNull
import studio.hcmc.reminisce.vo.user.UserVO

class EditFriendDialog(
    activity: Activity,
    opponentId: Int,
    savedNickname: String?,
    position: Int,
    delegate: Delegate
) {
    interface Delegate {
        fun getUser(userId: Int): UserVO
        fun onEditClick(opponentId: Int, body: String?, position: Int)
    }

    init {
        //TODO 수정
        val viewBinding = DialogEditFriendBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        // friend로 등록된 user의 nickname
        // TODO getUser -> userIO getById로 조회해서 들고 있기
        viewBinding.dialogEditFriendFieldEditText.hint = delegate.getUser(opponentId).nickname
        // friend로 등록된 user의 email
        viewBinding.dialogEditFriendField.helperText = delegate.getUser(opponentId).email
        // friend에 등록된 nickname
        viewBinding.dialogEditFriendTitle.text = savedNickname ?: delegate.getUser(opponentId).nickname

        viewBinding.apply {
            dialogEditFriendField.editText!!.addTextChangedListener {
                if (dialogEditFriendField.string.length > 20) {
                    dialogEditFriendSave.isEnabled = false
                }
                if (dialogEditFriendField.string.length <= 20) {
                    dialogEditFriendSave.isEnabled = true
                }
            }
        }
        viewBinding.dialogEditFriendSave.setOnClickListener {
            // TODO stringOrNull 수정
            val input = viewBinding.dialogEditFriendField.stringOrNull
//            when {
//                input.isNullOrEmpty() -> {
//
//                }
//                input.isNotEmpty() &&
//            }
//            when {
//                // friend nickname is null
//                input.isEmpty() -> {
//                    delegate.onEditClick(opponentId, null, position)
//                    dialog.dismiss()
//                }
//                // friend nickname is not null
//                input.isNotEmpty() && input.length <= 20 -> {
//                    delegate.onEditClick(opponentId, viewBinding.dialogEditFriendField.string, position)
//                    dialog.dismiss()
//                }
//            }
        }
        viewBinding.dialogEditFriendCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}