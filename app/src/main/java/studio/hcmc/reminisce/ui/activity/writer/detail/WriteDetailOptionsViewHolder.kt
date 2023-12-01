package studio.hcmc.reminisce.ui.activity.writer.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardWriteDetailOptionsBinding
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.tag.TagVO

class WriteDetailOptionsViewHolder(
    private val viewBinding: CardWriteDetailOptionsBinding,
): ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup): this(
        viewBinding = CardWriteDetailOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun  bind(content: WriteDetailAdapter.OptionsContent) {
        val (category, tags, friends) = content
        viewBinding.cardWriteDetailOptionsCategory.writeDetailItemIcon.setImageResource(R.drawable.round_folder_16)
        viewBinding.cardWriteDetailOptionsCategory.writeDetailItemBody.text = category.title
        if (tags != null) {
            prepareTag(tags)
        }
        if (friends != null) {
            prepareFriendTag(friends)
        }
    }

    private fun prepareTag(tags: List<TagVO>) {
        val tagText = tags?.withIndex()?.joinToString { it.value.body }
        if (!tags.isNullOrEmpty()) {
            viewBinding.cardWriteDetailOptionsTags.root.isVisible = true
            viewBinding.cardWriteDetailOptionsTags.writeDetailItemIcon.setImageResource(R.drawable.round_tag_16)
            viewBinding.cardWriteDetailOptionsTags.writeDetailItemBody.text = tagText
        } else {
            viewBinding.cardWriteDetailOptionsTags.root.isGone = true
        }
    }

    private fun prepareFriendTag(friends: List<FriendVO>) {
        val friendText = friends?.joinToString { it.nickname!! }
        if (!friends.isNullOrEmpty()) {
            viewBinding.cardWriteDetailOptionsFriends.root.isVisible = true
            viewBinding.cardWriteDetailOptionsFriends.writeDetailItemIcon.setImageResource(R.drawable.round_group_16)
            viewBinding.cardWriteDetailOptionsFriends.writeDetailItemBody.text = friendText
        } else {
            viewBinding.cardWriteDetailOptionsFriends.root.isGone = true
        }
    }
}