package studio.hcmc.reminisce.ui.activity.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardSummaryBinding
import studio.hcmc.reminisce.vo.user.UserVO

class TagDetailSummaryViewHolder(
    private val viewBinding: CardSummaryBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(locationId: Int, title: String)

        fun getUser(userId: Int): UserVO
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: TagDetailAdapter.DetailContent) {
        val (location, tags, friends) = content
        val (year, month, day) = location.visitedAt.split("-")
        viewBinding.apply {
            cardSummaryTitle.text = location.title
            cardSummaryVisitedAt.layoutSummaryItemBody.text = viewBinding.root.context.getString(R.string.card_visited_at, year, month.trim('0'), day.trim('0'))
            cardSummaryAddress.layoutSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
            cardSummaryAddress.layoutSummaryItemBody.text = location.roadAddress
            cardSummaryVisitedCount.root.isGone = true
        }

        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardSummaryMarkerEmoji.root.isVisible = true
            viewBinding.cardSummaryMarkerEmoji.layoutSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
            viewBinding.cardSummaryMarkerEmoji.layoutSummaryItemBody.text = location.markerEmoji
        } else {
            viewBinding.cardSummaryMarkerEmoji.root.isGone = true
        }

        val tagText = tags.withIndex().joinToString { it.value.body }
        if (tagText.isNotEmpty()) {
            viewBinding.cardSummaryTags.root.isVisible = true
            viewBinding.cardSummaryTags.layoutSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
            viewBinding.cardSummaryTags.layoutSummaryItemBody.text = tagText
        } else {
            viewBinding.cardSummaryTags.root.isGone = true
        }

        val friendText = friends.joinToString { it.nickname ?: delegate.getUser(it.opponentId).nickname }
        if (friendText.isNotEmpty()) {
            viewBinding.cardSummaryFriends.root.isVisible = true
            viewBinding.cardSummaryFriends.layoutSummaryItemIcon.setImageResource(R.drawable.round_group_12)
            viewBinding.cardSummaryFriends.layoutSummaryItemBody.text = friendText
        } else {
            viewBinding.cardSummaryFriends.root.isGone = true
        }

        viewBinding.root.setOnClickListener { delegate.onItemClick(location.id, location.title) }
    }
}
