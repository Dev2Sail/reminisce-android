package studio.hcmc.reminisce.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object globalError {
    private fun onErrorDialog(
        context: Context
    ) = CoroutineScope(Dispatchers.Main).launch {
        MaterialAlertDialogBuilder(context)
            .setTitle("오류 발생")
            .setMessage("오류가 발생했어요. 앱을 재실행 해주세요.")
            .setPositiveButton("확인") { _, _ -> }
            .setCancelable(false)
            .show()
    }

}
