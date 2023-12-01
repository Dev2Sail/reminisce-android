package studio.hcmc.reminisce.ui.activity.category.editable

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

class ItemViewHolder(
    private val viewBinding: CardCheckableSummaryBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(locationId: Int): Boolean
    }

    constructor(parent: ViewGroup, delegate: Delegate ): this(
        viewBinding = CardCheckableSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: CategoryEditableDetailAdapter.DetailContent) {
        val (location, tags, friends) = content
        prepareLocation(location)
        if (tags != null) {
            prepareTags(tags)
        }
        if (friends != null) {
            prepareFriends(friends)
        }
        viewBinding.cardCheckableSummaryCheckbox.setOnClickListener {
            delegate.onItemClick(location.id)
        }
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
            viewBinding.cardCheckableSummaryAddress.root.isVisible = true
            viewBinding.cardCheckableSummaryAddress.layoutSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
            viewBinding.cardCheckableSummaryAddress.layoutSummaryItemBody.text = location.roadAddress
        } else { viewBinding.cardCheckableSummaryAddress.root.isGone = true }

        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardCheckableSummaryMarkerEmoji.root.isVisible = true
            viewBinding.cardCheckableSummaryMarkerEmoji.layoutSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
            viewBinding.cardCheckableSummaryMarkerEmoji.layoutSummaryItemBody.text = location.markerEmoji
        } else { viewBinding.cardCheckableSummaryMarkerEmoji.root.isGone = true }
    }

    private fun prepareTags(tags: List<TagVO>) {
        val tagText = tags.withIndex().joinToString { it.value.body }
        if (tagText.isNotEmpty()) {
            viewBinding.cardCheckableSummaryTags.root.isVisible = true
            viewBinding.cardCheckableSummaryTags.layoutSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
            viewBinding.cardCheckableSummaryTags.layoutSummaryItemBody.text = tagText
        } else { viewBinding.cardCheckableSummaryTags.root.isGone = true }
    }

    private fun prepareFriends(friends: List<FriendVO>) {
        val friendText = friends.joinToString { it.nickname!! }
        if (friendText.isNotEmpty()) {
            viewBinding.cardCheckableSummaryFriends.root.isVisible = true
            viewBinding.cardCheckableSummaryFriends.layoutSummaryItemIcon.setImageResource(R.drawable.round_group_12)
            viewBinding.cardCheckableSummaryFriends.layoutSummaryItemBody.text = friendText
        } else { viewBinding.cardCheckableSummaryFriends.root.isGone = true }
    }
}