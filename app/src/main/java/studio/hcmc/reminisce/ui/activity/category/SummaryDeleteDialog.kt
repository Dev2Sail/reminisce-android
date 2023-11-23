package studio.hcmc.reminisce.ui.activity.category

import android.content.Context
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class SummaryDeleteDialog(
    context: Context,
    delegate: Delegate,
    locationId: Int,
    position: Int
) {
    interface Delegate {
        fun onClick(locationId: Int, position: Int)
    }

    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = context.getText(R.string.dialog_summary_delete)
        viewBinding.dialogHomeCategoryDeleteBody.text = context.getText(R.string.dialog_summary_delete_body)
        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            delegate.onClick(locationId, position)
        }
        dialog.show()
    }
}