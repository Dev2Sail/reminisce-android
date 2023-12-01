package studio.hcmc.reminisce.ui.activity.home

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.databinding.DialogAddCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string

class AddCategoryDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onSaveClick(body: String?)
    }

    init {
        val viewBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogAddCategoryField.editText!!.addTextChangedListener {
            if (viewBinding.dialogAddCategoryField.string.length <= 15) {
                viewBinding.dialogAddCategorySave.isEnabled = true
            }
            if (viewBinding.dialogAddCategoryField.string.length > 15) {
                viewBinding.dialogAddCategorySave.isEnabled = false
            }
        }
        viewBinding.dialogAddCategorySave.setOnClickListener {
            dialog.dismiss()
            delegate.onSaveClick(viewBinding.dialogAddCategoryField.string)
        }
        viewBinding.dialogAddCategoryCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
