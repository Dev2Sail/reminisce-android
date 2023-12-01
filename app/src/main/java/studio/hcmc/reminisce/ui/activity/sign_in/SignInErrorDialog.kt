package studio.hcmc.reminisce.ui.activity.sign_in

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogSignInErrorBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class SignInErrorDialog(activity: Activity) {
    init {
        val viewBinding = DialogSignInErrorBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogSignInErrorOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}