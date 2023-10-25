package studio.hcmc.reminisce.ui.activity.category.editable

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.databinding.DialogEditCategoryTitleBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class CategoryTitleEditDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onSaveClick(editedTitle: String?)
    }

    init {
        val viewBinding = DialogEditCategoryTitleBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        val inputField = viewBinding.dialogCategoryTitleField
        viewBinding.dialogEditCategorySave.isEnabled = false
        inputField.editText!!.addTextChangedListener {
            viewBinding.dialogEditCategorySave.isEnabled = inputField.text.isNotEmpty() && inputField.string.length <= 15
        }

        dialog.show()

        viewBinding.dialogEditCategorySave.setOnClickListener {
            if (viewBinding.dialogCategoryTitleField.string.length <= 15) {
                delegate.onSaveClick(viewBinding.dialogCategoryTitleField.string)
            }
            dialog.dismiss()
        }
        viewBinding.dialogEditCategoryCancel.setOnClickListener { dialog.dismiss() }
    }
}