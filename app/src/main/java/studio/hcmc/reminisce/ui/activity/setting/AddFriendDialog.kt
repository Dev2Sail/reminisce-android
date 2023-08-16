package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogHomeCategoryDeleteBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class AddFriendDialog(activity: Activity) {
    init {
        val viewBinding = DialogHomeCategoryDeleteBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = "친구 추가"
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
