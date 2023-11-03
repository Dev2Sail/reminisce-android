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
        fun onTagClick()
        fun onFriendClick()
        fun onCategoryClick()
    }

    private var viewBinding: DialogWriteOptionsBinding

    init {
        viewBinding = DialogWriteOptionsBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.writeOptionsNextFriend.setOnClickListener { delegate.onFriendClick() }
        viewBinding.writeOptionsNextTag.setOnClickListener { delegate.onTagClick() }
        viewBinding.writeOptionsNextCategory.setOnClickListener { delegate.onCategoryClick() }
        dialog.show()
    }
}
