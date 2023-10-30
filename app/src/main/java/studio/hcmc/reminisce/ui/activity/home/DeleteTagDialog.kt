package studio.hcmc.reminisce.ui.activity.home

import android.content.Context
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class DeleteTagDialog(
    context: Context,
    tagId: Int,
    tagIdx: Int,
    position: Int,
    delegate: Delegate
) {
    interface Delegate {
        fun onDeleteClick(tagId: Int, tagIdx: Int, position: Int)
    }

    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = context.getString(R.string.dialog_delete_tag_title)
        viewBinding.dialogHomeCategoryDeleteBody.text = context.getString(R.string.dialog_delete_tag_body)
        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            delegate.onDeleteClick(tagId, position, tagIdx)
        }
        dialog.show()
    }
}