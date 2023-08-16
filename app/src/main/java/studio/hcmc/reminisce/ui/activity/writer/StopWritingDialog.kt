package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogHomeCategoryDeleteBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class StopWritingDialog(activity: Activity) {
    init {
        val viewBinding = DialogHomeCategoryDeleteBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = "그만 작성하기"
        viewBinding.dialogHomeCategoryDeleteBody.text = "현재 작성하던 내용은 저장되지 않아요."
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
