package studio.hcmc.reminisce.ui.activity.writer.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardWriteDetailOptionsBinding
import studio.hcmc.reminisce.vo.user.UserVO

class WriteDetailOptionsViewHolder(
    private val viewBinding: CardWriteDetailOptionsBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun getUser(userId: Int): UserVO
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardWriteDetailOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun  bind(content: WriteDetailAdapter.OptionsContent) {
        val (category, tags, friends) = content
        viewBinding.cardWriteDetailOptionsCategory.apply {
            writeDetailItemIcon.setImageResource(R.drawable.round_folder_16)
            writeDetailItemBody.text = category.title
        }

        val tagText = tags?.withIndex()?.joinToString { it.value.body }
        if (!tags.isNullOrEmpty()) {
            viewBinding.cardWriteDetailOptionsTags.root.isVisible = true
            viewBinding.cardWriteDetailOptionsTags.writeDetailItemIcon.setImageResource(R.drawable.round_tag_16)
            viewBinding.cardWriteDetailOptionsTags.writeDetailItemBody.text = tagText
        } else {
            viewBinding.cardWriteDetailOptionsTags.root.isGone = true
        }

        val friendText = friends?.joinToString { it.nickname ?: delegate.getUser(it.opponentId).nickname }
        if (!friends.isNullOrEmpty()) {
            viewBinding.cardWriteDetailOptionsFriends.root.isVisible = true
            viewBinding.cardWriteDetailOptionsFriends.writeDetailItemIcon.setImageResource(R.drawable.round_group_16)
            viewBinding.cardWriteDetailOptionsFriends.writeDetailItemBody.text = friendText
        } else {
            viewBinding.cardWriteDetailOptionsFriends.root.isGone = true
        }
    }
}