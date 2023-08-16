package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogSelectTagFriendBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class SelectFriendTagDialog(
    activity: Activity
) {
    init {
        val viewBinding = DialogSelectTagFriendBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
    }
}