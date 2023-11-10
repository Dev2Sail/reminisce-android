package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogWriteOptionsBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class WriteOptionsDialog(
    activity: Activity,
    delegate: Delegate,
    categoryName: String
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
        viewBinding.writeOptionsCategoryName.text = when(categoryName) {
            "Default" -> viewBinding.root.context.getString(R.string.category_view_holder_title)
            "new" -> viewBinding.root.context.getString(R.string.add_category_body)
            else -> categoryName
        }
        dialog.show()
    }
}
