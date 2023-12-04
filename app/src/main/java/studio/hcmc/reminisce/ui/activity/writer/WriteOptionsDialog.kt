package studio.hcmc.reminisce.ui.activity.writer

import android.content.Context
import android.view.LayoutInflater
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogWriteOptionsBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class WriteOptionsDialog(private val delegate: Delegate) {
    private val viewBinding = DialogWriteOptionsBinding.inflate(LayoutInflater.from(delegate.context))
    private val dialog = BottomSheetDialog(delegate.context, viewBinding)

    interface Delegate {
        val context: Context
        var friends: ArrayList<String>
        var tags: ArrayList<String>
        var categoryTitle: String

        fun isFriendAdded(): Boolean
        fun isTagAdded(): Boolean
        fun isCategoryModified(): Boolean
        fun onTagClick()
        fun onFriendClick()
        fun onCategoryClick()
    }

    init {
        viewBinding.writeOptionsNextFriend.setOnClickListener {
            delegate.onFriendClick()
            dialog.dismiss()
        }
        viewBinding.writeOptionsNextTag.setOnClickListener {
            delegate.onTagClick()
            dialog.dismiss()
        }
        viewBinding.writeOptionsNextCategory.setOnClickListener {
            delegate.onCategoryClick()
            dialog.dismiss()
        }
    }

    fun show() {
        viewBinding.writeOptionsFriendBody.text = delegate.friends.joinToString(" ")
        viewBinding.writeOptionsTagBody.text = delegate.tags.joinToString(" ")
        viewBinding.writeOptionsCategoryBody.text = when (delegate.categoryTitle) {
            "Default" -> viewBinding.root.context.getString(R.string.category_view_holder_title)
            "new" -> viewBinding.root.context.getString(R.string.add_category_body)
            else -> delegate.categoryTitle
        }

        if (delegate.isFriendAdded()) {
            viewBinding.writeOptionsFriendIcon.setImageResource(R.drawable.round_favorite_24)
        }
        if (delegate.isTagAdded()) {
            viewBinding.writeOptionsTagIcon.setImageResource(R.drawable.round_favorite_24)
        }
        if (delegate.isCategoryModified()) {
            viewBinding.writeOptionsCategoryIcon.setImageResource(R.drawable.round_favorite_24)
        }
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
