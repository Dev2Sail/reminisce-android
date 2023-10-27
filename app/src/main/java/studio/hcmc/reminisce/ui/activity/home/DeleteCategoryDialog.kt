package studio.hcmc.reminisce.ui.activity.home

import android.content.Context
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class DeleteCategoryDialog(
    context: Context,
    categoryId: Int,
    position: Int,
    delegate: Delegate
) {


    interface Delegate {
        fun onDeleteClick(categoryId: Int, position: Int)
    }
    init {
        val viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context, viewBinding)

        dialog.show()

       viewBinding.apply {
           dialogHomeCategoryDeleteCancel.setOnClickListener {
               dialog.dismiss()
           }
           dialogHomeCategoryDeleteRemove.setOnClickListener {
               dialog.dismiss()
               delegate.onDeleteClick(categoryId, position)
           }
       }
    }
}
