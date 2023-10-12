package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.databinding.DialogUpdateFriendNicknameBinding
import studio.hcmc.reminisce.dto.friend.FriendDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.vo.friend.FriendVO

class UpdateFriendNicknameDialog(
    activity: Activity,
    context: Context,
    friend: FriendVO,
    opponentNickname: String,
    opponentEmail: String
) {
    private val responseContext = context

    init {
        val viewBinding = DialogUpdateFriendNicknameBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        val inputField = viewBinding.dialogUpdateFriendNicknameField.apply {
            hint = friend.nickname ?: ""
            helperText = opponentEmail
            placeholderText = opponentNickname
        }

        dialog.show()

        inputField.editText!!.addTextChangedListener {
            viewBinding.dialogUpdateFriendNicknameSave.isEnabled = inputField.string.isNotEmpty() && inputField.string.length <= 20
        }

        viewBinding.dialogUpdateFriendNicknameSave.setOnClickListener {
            if (inputField.string.length <= 20) {
                patchFriend(friend.opponentId, inputField.string)
                dialog.dismiss()
            }
        }
        viewBinding.dialogUpdateFriendNicknameCancel.setOnClickListener { dialog.dismiss() }
    }

    private fun patchFriend(opponentId: Int, editedNickname: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(responseContext)
        val putDTO = FriendDTO.Put().apply { nickname = editedNickname }
        runCatching { FriendIO.put(user.id, opponentId, putDTO) }
            .onFailure {
                Log.v("reminisce Logger", "[reminisce > Friend Setting > patchFriend] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }
}