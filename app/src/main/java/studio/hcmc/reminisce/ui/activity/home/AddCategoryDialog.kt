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
        viewBinding.apply {
            dialogAddCategoryField.editText!!.addTextChangedListener {
                if (dialogAddCategoryField.string.length <= 15) {
                    dialogAddCategorySave.isEnabled = true
                }
                if (dialogAddCategoryField.string.length > 15) {
                    dialogAddCategorySave.isEnabled = false
                }
            }
        }
        viewBinding.dialogAddCategorySave.setOnClickListener {
            val input = viewBinding.dialogAddCategoryField.string
            when {
                input.isEmpty() -> {
                    delegate.onSaveClick(null)
                    dialog.dismiss()
                }
                input.isNotEmpty() -> {
                    delegate.onSaveClick(input)
                    dialog.dismiss()
                }
            }
        }
        viewBinding.dialogAddCategoryCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
