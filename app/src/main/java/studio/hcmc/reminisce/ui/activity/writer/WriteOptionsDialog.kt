package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogWriteOptionsBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class WriteOptionsDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun addTagClick()
        fun addFriendClick()
        fun selectCategoryClick()
    }

    init {
        val viewBinding = DialogWriteOptionsBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.writeOptionsNextFriend.setOnClickListener { delegate.addFriendClick() }
        viewBinding.writeOptionsNextTag.setOnClickListener { delegate.addTagClick() }
        viewBinding.writeOptionsNextCategory.setOnClickListener { delegate.selectCategoryClick() }
        dialog.show()
    }
}