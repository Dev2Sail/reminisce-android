package studio.hcmc.reminisce.ui.activity.friend_tag.editable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCheckableSummaryBinding
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class SummaryViewHolder(
    private val viewBinding: CardCheckableSummaryBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(locationId: Int): Boolean
    }

    constructor(parent: ViewGroup, delegate: Delegate): this (
        viewBinding = CardCheckableSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: FriendTagEditableAdapter.DetailContent) {
        val (location, tags, friends) = content
        prepareLocation(location)
        tags?.let { prepareTags(it) }
        prepareFriends(friends)
        viewBinding.cardCheckableSummaryCheckbox.setOnClickListener { delegate.onItemClick(location.id) }
        viewBinding.cardCheckableSummaryContainer.setOnClickListener {
            viewBinding.cardCheckableSummaryCheckbox.isChecked = delegate.onItemClick(location.id)
        }
    }

    private fun prepareLocation(location: LocationVO) {
        viewBinding.cardCheckableSummaryTitle.text = location.title
        val (year, month, day) = location.visitedAt.split("-")
        viewBinding.cardCheckableSummaryVisitedAt.layoutSummaryItemBody.text = viewBinding.root.context.getString(R.string.card_visited_at, year, month.trim('0'), day.trim('0'))
        viewBinding.cardCheckableSummaryVisitedCount.root.isGone = true

        if (location.roadAddress.isNotEmpty()) {
            viewBinding.cardCheckableSummaryAddress.apply {
                root.isVisible = true
                layoutSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
                layoutSummaryItemBody.text = location.roadAddress
            }
        } else { viewBinding.cardCheckableSummaryAddress.root.isGone = true }

        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardCheckableSummaryMarkerEmoji.apply {
                root.isVisible = true
                layoutSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
                layoutSummaryItemBody.text = location.markerEmoji
            }
        } else { viewBinding.cardCheckableSummaryMarkerEmoji.root.isGone = true }
    }

    private fun prepareTags(tags: List<TagVO>) {
        val tagText = tags.withIndex().joinToString { it.value.body }
        if (tagText.isNotEmpty()) {
            viewBinding.cardCheckableSummaryTags.apply {
                root.isVisible = true
                layoutSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
                layoutSummaryItemBody.text = tagText
            }
        } else { viewBinding.cardCheckableSummaryTags.root.isGone = true }
    }

    private fun prepareFriends(friends: List<FriendVO>) {
        val friendText = friends.joinToString { it.nickname!! }
        if (friendText.isNotEmpty()) {
            viewBinding.cardCheckableSummaryFriends.apply {
                root.isVisible = true
                layoutSummaryItemIcon.setImageResource(R.drawable.round_group_12)
                layoutSummaryItemBody.text = friendText
            }
        } else { viewBinding.cardCheckableSummaryFriends.root.isGone = true }
    }
}