package studio.hcmc.reminisce.ui.activity.category.editable

import android.content.Context
import android.view.LayoutInflater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogDeleteHomeCategoryBinding
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class SummaryDeleteDialog(
    context: Context,
    locationId: Int
) {
    private val viewBinding: DialogDeleteHomeCategoryBinding
    private val selectedLocationId = locationId

    init {
        viewBinding = DialogDeleteHomeCategoryBinding.inflate(LayoutInflater.from(context))
        val dialog = BottomSheetDialog(context, viewBinding)

        viewBinding.apply {
            dialogHomeCategoryDeleteTitle.text = context.getText(R.string.dialog_summary_delete)
            dialogHomeCategoryDeleteBody.text = context.getText(R.string.dialog_summary_delete_body)
        }


        viewBinding.apply {
            dialogHomeCategoryDeleteCancel.setOnClickListener { dialog.dismiss() }
            dialogHomeCategoryDeleteRemove.setOnClickListener {
                dialog.dismiss()
                deleteSummary()
            }
        }
        dialog.show()
    }

    private fun deleteSummary() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.delete(selectedLocationId) }
            .onSuccess {

            }
    }

}