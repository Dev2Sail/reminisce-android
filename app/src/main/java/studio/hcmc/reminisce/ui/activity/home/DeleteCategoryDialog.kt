package studio.hcmc.reminisce.ui.activity.home

import android.content.Context
import android.view.LayoutInflater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.LocalLogger

class DeleteCategoryDialog(
    context: Context,
    categoryId: Int
) {
    private val selectedCategoryId = categoryId
    private val viewBinding: DialogDeleteHomeCategoryBinding
    init {
        viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context, viewBinding)

        dialog.show()

       viewBinding.apply {
           dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
           dialogHomeCategoryDeleteRemove.setOnClickListener {
               dialog.dismiss()
               deleteCategory()
           }
       }
    }
    private fun deleteCategory() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { CategoryIO.delete(selectedCategoryId) }
            .onSuccess {
                // TODO recyclerView notify()
            }
            .onFailure {
                LocalLogger.e(it)
            }
    }
}
