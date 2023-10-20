package studio.hcmc.reminisce.ui.activity.tag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCategoryDetailSummaryItemBinding
import studio.hcmc.reminisce.databinding.CardTagDetailSummaryBinding

class TagDetailItemViewHolder(
    private val viewBinding: CardCategoryDetailSummaryItemBinding
): ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup): this(
        viewBinding = CardCategoryDetailSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(content: TagDetailAdapter.TagContents) {
        // Summary에 추가될 수 있는 data
        // tags
        val summaryView = CardTagDetailSummaryBinding.inflate(LayoutInflater.from(viewBinding.root.context)).cardTagDetailSummaryContainer
        val emojiContent = (content as? TagDetailAdapter.TagDetailContent)?.location?.markerEmoji
        if (emojiContent != null) {
            summaryView.addView(addEmoji(emojiContent))
        }

        // tags
        val tagContent = (content as? TagDetailAdapter.TagDetailTagItemContent)
        if (tagContent != null) {
            // StringBuilder 로 tags만 넘겨야 함
//            summaryView.addView(addTag(tagContent))
        }

        // friends
        val friendContent = (content as? TagDetailAdapter.TagDetailFriendItemContent)
        // StringBuilder 로 friends만 넘겨야 함
        if (friendContent != null) {
//            summaryView.addView(addFriend(friendContent))
        }
    }

    private fun addEmoji(body: String): View {
        viewBinding.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
        viewBinding.cardCategoryDetailSummaryItemTitle.text = body
        viewBinding.root.setPadding(0, R.dimen.margin_small, 0, 0)

        return viewBinding.root
    }

    private fun addTag(body: String): View {
        viewBinding.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
        viewBinding.cardCategoryDetailSummaryItemTitle.text = body
        viewBinding.root.setPadding(0, R.dimen.margin_small, 0, 0)

        return viewBinding.root
    }

    private fun addFriend(body: String): View {
        viewBinding.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_group_12)
        viewBinding.cardCategoryDetailSummaryItemTitle.text = body
        viewBinding.root.setPadding(0, R.dimen.margin_small, 0, 0)

        return viewBinding.root
    }
}
