package studio.hcmc.reminisce.ui.activity.sign_up

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogSignInErrorBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class SignUpMessageDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onDoneClick()
    }
    init {
        val viewBinding = DialogSignInErrorBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogSignInErrorTitle.text = activity.getText(R.string.dialog_sign_up_message_title)
        viewBinding.dialogSignInErrorBody.text = activity.getText(R.string.dialog_sign_up_message_body)
        viewBinding.dialogSignInErrorOk.setOnClickListener {
            dialog.dismiss()
            delegate.onDoneClick()
        }
        dialog.show()
    }
}