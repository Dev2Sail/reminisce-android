package studio.hcmc.reminisce.ui.activity.sign_up

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogSignInErrorBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class SignUpErrorDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onDoneClick()
    }
    init {
        val viewBinding = DialogSignInErrorBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogSignInErrorTitle.text = activity.getText(R.string.dialog_sign_up_error_title)
        viewBinding.dialogSignInErrorBody.text = activity.getText(R.string.dialog_sign_up_error_body)
        viewBinding.dialogSignInErrorOk.setOnClickListener {
            dialog.dismiss()
            delegate.onDoneClick()
        }
        dialog.show()
    }
}