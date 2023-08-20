package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.databinding.DialogUpdateFriendNicknameBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string

class UpdateFriendNicknameDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onSaveClick(nickname: String)
    }

    init {
        val viewBinding = DialogUpdateFriendNicknameBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        val inputField = viewBinding.dialogUpdateFriendNicknameField

        dialog.show()

        inputField.editText!!.addTextChangedListener {
            viewBinding.dialogUpdateFriendNicknameSave.isEnabled = inputField.string.isNotEmpty()
        }
        viewBinding.dialogUpdateFriendNicknameSave.setOnClickListener {
            val inputtedValue = inputField.string
            if (inputtedValue.length <= 20) {
                dialog.dismiss()
                delegate.onSaveClick(inputtedValue)
            }
        }
        viewBinding.dialogUpdateFriendNicknameCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
}