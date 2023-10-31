package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class StopWritingDialog(activity: Activity) {
    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogHomeCategoryDeleteTitle.text = activity.getText(R.string.dialog_stop_writing_title)
        viewBinding.dialogHomeCategoryDeleteBody.text = activity.getText(R.string.dialog_stop_writing_body)
        viewBinding.dialogHomeCategoryDeleteCancel.text = activity.getText(R.string.dialog_stop_writing_cancel)
        viewBinding.dialogHomeCategoryDeleteRemove.text = activity.getText(R.string.dialog_stop_writing_ok)
        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            activity.finish()
        }
        dialog.show()
    }
}
