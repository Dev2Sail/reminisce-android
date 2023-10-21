package studio.hcmc.reminisce.ui.activity.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardTagSummaryBinding
import studio.hcmc.reminisce.vo.user.UserVO

class TagDetailSummaryViewHolder(
    private val viewBinding: CardTagSummaryBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(locationId: Int)

        fun getUser(userId: Int): UserVO
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardTagSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: TagDetailAdapter.TagDetailContent) {
        val (location, tags, friends) = content
        val addressBuilder = StringBuilder()
        // TODO 좌표 -> 주소로 변환 후 getString에서 정해둔 포맷으로 넣으셈! (date_separator) 참고
        addressBuilder.append(location.latitude)
        addressBuilder.append(location.longitude)

        viewBinding.apply {
            cardTagDetailSummaryTitle.text = location.title
            cardTagDetailSummaryVisitedAt.cardCategoryDetailSummaryItemTitle.text = location.visitedAt
            cardTagDetailSummaryAddress.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
            cardTagDetailSummaryAddress.cardCategoryDetailSummaryItemTitle.text = addressBuilder.toString()
        }

        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardTagDetailSummaryMarkerEmoji.root.isVisible = true
            viewBinding.cardTagDetailSummaryMarkerEmoji.apply {
                cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
                cardCategoryDetailSummaryItemTitle.text = location.markerEmoji
            }
        } else {
            viewBinding.cardTagDetailSummaryMarkerEmoji.root.isGone = true
        }

        val tagText = tags.withIndex().joinToString { it.value.body }
        if (tagText.isNotEmpty()) {
            viewBinding.cardTagDetailSummaryTags.root.isVisible = true
            viewBinding.cardTagDetailSummaryTags.apply {
                cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
                cardCategoryDetailSummaryItemTitle.text = tagText
            }
        } else {
            viewBinding.cardTagDetailSummaryTags.root.isGone = true
        }

        val friendText = friends.joinToString { it.nickname ?: delegate.getUser(it.opponentId).nickname }
        if (friendText.isNotEmpty()) {
            viewBinding.cardTagDetailSummaryFriends.root.isVisible = true
            viewBinding.cardTagDetailSummaryFriends.apply {
                cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_group_12)
                cardCategoryDetailSummaryItemTitle.text = friendText
            }
        } else {
            viewBinding.cardTagDetailSummaryFriends.root.isGone = true
        }

        viewBinding.root.setOnClickListener {
            delegate.onItemClick(location.id)
        }
    }
}
