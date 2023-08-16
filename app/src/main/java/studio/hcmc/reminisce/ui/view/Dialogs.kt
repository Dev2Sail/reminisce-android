package studio.hcmc.reminisce.ui.view

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

inline fun BottomSheetDialog(
    context: Context,
    viewBinding: ViewBinding,
    configure: BottomSheetDialog.() -> Unit = {}
): BottomSheetDialog {
    val dialog = BottomSheetDialog(context)
    dialog.setContentView(viewBinding.root)
    dialog.configure()

    return dialog
}