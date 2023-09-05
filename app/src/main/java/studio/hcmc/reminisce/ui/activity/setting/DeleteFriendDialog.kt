package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
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
        viewBinding.dialogHomeCategoryDeleteTitle.text = activity.getText(R.string.dialog_delete_friend_title)
        viewBinding.dialogHomeCategoryDeleteBody.text = activity.getText(R.string.dialog_delete_friend_body)

        dialog.show()

        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener {
            dialog.dismiss()
        }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            deleteFriend(opponentId)
            dialog.dismiss()
        }
    }

    private fun deleteFriend(opponentId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(viewBinding.root.context)
        runCatching { FriendIO.delete(user.id, opponentId) }
            .onSuccess { CommonMessage.onMessage(viewBinding.root.context, "친구가 끊어졌어요.") }
            .onFailure {
                CommonError.onDialog(viewBinding.root.context)
                it.cause
                it.message
                it.stackTrace
            }
    }
}