package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.ui.view.CommonMessage

class DeleteFriendDialog(
    activity: Activity,
    opponentId: Int
) {
    private val viewBinding: DialogDeleteHomeCategoryBinding
    init {
        viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.apply {
            dialogHomeCategoryDeleteTitle.text = activity.getText(R.string.dialog_delete_friend_title)
            dialogHomeCategoryDeleteBody.text = activity.getText(R.string.dialog_delete_friend_body)
        }

        dialog.show()

        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            deleteFriend(opponentId)
            dialog.dismiss()
        }
    }

    private fun deleteFriend(opponentId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(viewBinding.root.context)
        runCatching { FriendIO.delete(user.id, opponentId) }
            .onSuccess { CommonMessage.onMessage(viewBinding.root.context, "친구가 삭제되었어요.") }
            .onFailure {
                CommonError.onDialog(viewBinding.root.context)
                Log.v("reminisce Logger", "[reminisce > Setting > Delete Friend > deleteFriend] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }
}