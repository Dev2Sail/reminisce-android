package studio.hcmc.reminisce.ui.activity.home

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

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

        viewBinding.dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
        viewBinding.dialogHomeCategoryDeleteRemove.setOnClickListener {
            dialog.dismiss()
            deleteCategory()
        }
    }
    private fun deleteCategory() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { CategoryIO.delete(selectedCategoryId) }
            .onSuccess {
                onDeleteMessage(viewBinding.root.context)
            }
            .onFailure {
                it.cause
                it.message
                it.stackTrace
            }
    }

    private fun onDeleteMessage(context: Context) = CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(context, "폴더가 삭제되었어요", Toast.LENGTH_SHORT).show()
    }
}