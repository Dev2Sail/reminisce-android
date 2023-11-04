package studio.hcmc.reminisce.ui.activity.category.editable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCheckableSummaryBinding
import studio.hcmc.reminisce.vo.user.UserVO

class SummaryViewHolder(
    private val viewBinding: CardCheckableSummaryBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(locationId: Int): Boolean
        fun getUser(userId: Int): UserVO
        fun getAddress(locationId: Int): String
    }

    constructor(parent: ViewGroup, delegate: Delegate ): this(
        viewBinding = CardCheckableSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: CategoryEditableDetailAdapter.DetailContent) {
        val (location, tags, friends) = content
        val (year, month, day) = location.visitedAt.split("-")
        viewBinding.cardCheckableSummaryTitle.text = location.title
        viewBinding.cardCheckableSummaryVisitedAt.layoutSummaryItemBody.text = viewBinding.root.context.getString(R.string.card_visited_at, year, month.trim('0'), day.trim('0'))
        viewBinding.cardCheckableSummaryAddress.layoutSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
        viewBinding.cardCheckableSummaryAddress.layoutSummaryItemBody.text = delegate.getAddress(location.id)
        viewBinding.cardCheckableSummaryVisitedCount.root.isGone = true

        val tagText = tags.withIndex().joinToString { it.value.body }
        if (tagText.isNotEmpty()) {
            viewBinding.cardCheckableSummaryTags.root.isVisible = true
            viewBinding.cardCheckableSummaryTags.layoutSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
            viewBinding.cardCheckableSummaryTags.layoutSummaryItemBody.text = tagText
        } else { viewBinding.cardCheckableSummaryTags.root.isGone = true }

        val friendText = friends.joinToString { it.nickname ?: delegate.getUser(it.opponentId).nickname }
        if (friendText.isNotEmpty()) {
            viewBinding.cardCheckableSummaryFriends.root.isVisible = true
            viewBinding.cardCheckableSummaryFriends.layoutSummaryItemIcon.setImageResource(R.drawable.round_group_12)
            viewBinding.cardCheckableSummaryFriends.layoutSummaryItemBody.text = friendText
        } else { viewBinding.cardCheckableSummaryFriends.root.isGone = true }

        viewBinding.cardCheckableSummaryCheckbox.setOnClickListener { delegate.onItemClick(location.id) }
        viewBinding.cardCheckableSummaryContainer.setOnClickListener {
            viewBinding.cardCheckableSummaryCheckbox.isChecked = delegate.onItemClick(location.id)
        }
    }
}
/*
if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardCheckableSummaryMarkerEmoji.root.isVisible = true
            viewBinding.cardCheckableSummaryMarkerEmoji.apply {
                layoutSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
                layoutSummaryItemBody.text = location.markerEmoji
            }
        } else {
            viewBinding.cardCheckableSummaryMarkerEmoji.root.isGone = true
        }
 */