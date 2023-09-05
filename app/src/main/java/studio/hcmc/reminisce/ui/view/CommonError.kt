package studio.hcmc.reminisce.ui.view

import android.content.Context
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.BuildConfig

object CommonError {
    fun onDialog(
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

    fun debugError(it: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e("reminisce", it.message, it)
        } else {
            // TODO Remote logger
        }
    }
}
