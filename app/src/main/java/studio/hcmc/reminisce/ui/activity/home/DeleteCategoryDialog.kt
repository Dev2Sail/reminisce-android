package studio.hcmc.reminisce.ui.activity.home

import android.content.Context
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class DeleteCategoryDialog(
    context: Context,
    delegate: Delegate
) {
    private val viewBinding: DialogDeleteHomeCategoryBinding

    interface Delegate {
        fun onDeleteClick()
    }
    init {
        viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context, viewBinding)

        dialog.show()

       viewBinding.apply {
           dialogHomeCategoryDeleteCancel.setOnClickListener {
               dialog.dismiss()
           }
           dialogHomeCategoryDeleteRemove.setOnClickListener {
               dialog.dismiss()
               delegate.onDeleteClick()
           }
       }
    }
//    private fun deleteCategory() = CoroutineScope(Dispatchers.IO).launch {
//        runCatching { CategoryIO.delete(selectedCategoryId) }
//            .onSuccess {
//                // TODO recyclerView notify()
//
//            }
//            .onFailure {
//                LocalLogger.e(it)
//            }
//    }
}
