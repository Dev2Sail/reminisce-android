package studio.hcmc.reminisce.ui.view

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CommonError {
    fun onrDialog(
        context: Context
    ) = CoroutineScope(Dispatchers.Main).launch {
        MaterialAlertDialogBuilder(context)
            .setTitle("오류 발생")
            .setMessage("오류가 발생했어요. 앱을 재실행 해주세요.")
            .setPositiveButton("확인") { _, _ -> }
            .setCancelable(false)
            .show()
    }

    fun onMessageDialog(
        context: Context,
        title: String,
        message: String
    ) = CoroutineScope(Dispatchers.Main).launch {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> }
            .setCancelable(true)
            .show()
    }
}
