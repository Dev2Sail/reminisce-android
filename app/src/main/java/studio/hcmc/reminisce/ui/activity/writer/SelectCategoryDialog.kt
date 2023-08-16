package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogSelectCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class SelectCategoryDialog(
    activity: Activity
) {
    init {
        val viewBinding = DialogSelectCategoryBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)


    }
}