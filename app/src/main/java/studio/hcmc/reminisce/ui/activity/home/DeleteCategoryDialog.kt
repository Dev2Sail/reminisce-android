package studio.hcmc.reminisce.ui.activity.home

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import studio.hcmc.reminisce.databinding.DialogHomeCategoryDeleteBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class DeleteCategoryDialog(context: Context) {
    init {
        val viewBinding = DialogHomeCategoryDeleteBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = "폴더 삭제"
        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener {
            dialog.dismiss()
        }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            Toast.makeText(it.context, "폴더가 삭제되었어요.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }
}