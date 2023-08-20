package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class AddFriendDialog(activity: Activity) {
    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
//        viewBinding.dialogHomeCategoryDeleteTitle.text =
        viewBinding.dialogHomeCategoryDeleteBody.text = "(닉네임)님을 친구로 등록할까요?"
        viewBinding.dialogHomeCategoryDeleteCancel.text = "아니요"
        viewBinding.dialogHomeCategoryDeleteRemove.text = "네"

        dialog.show()

        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener {
            dialog.dismiss()
        }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            activity.finish()
        }
    }
}
// dialog 따로 만들거나  getText 방법 찾아보셈