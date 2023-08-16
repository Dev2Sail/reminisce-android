package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogHomeCategoryDeleteBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class WithdrawDialog(activity: Activity) {
    init {
        val viewBinding = DialogHomeCategoryDeleteBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = "서비스 탈퇴"
        viewBinding.dialogHomeCategoryDeleteBody.text = "오늘추억을 탈퇴하시겠어요? \n\n - 기록된 모든 추억은 복구할 수 없어요. \n - 해당 이메일로 재가입이 어려워요."
        viewBinding.dialogHomeCategoryDeleteCancel.text = "취소"
        viewBinding.dialogHomeCategoryDeleteRemove.text = "탈퇴"

        dialog.show()

        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener {
            dialog.dismiss()
        }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            // launcher로 이동시켜야 함...!
            activity.finish()
        }
    }
}