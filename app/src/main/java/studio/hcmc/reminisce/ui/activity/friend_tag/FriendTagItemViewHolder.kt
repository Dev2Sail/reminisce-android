package studio.hcmc.reminisce.ui.activity.friend_tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardSummaryBinding
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class FriendTagItemViewHolder(
    private val viewBinding: CardSummaryBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(locationId: Int, title: String)
        fun onItemLongClick(locationId: Int, position: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: FriendTagAdapter.DetailContent) {
        val (location, tags, friends) = content
        prepareLocation(location)
        if (tags != null) {
            prepareTags(tags)
        }
        prepareFriends(friends)
        viewBinding.root.setOnClickListener {
            delegate.onItemClick(location.id, location.title)
        }
        viewBinding.root.setOnLongClickListener {
            delegate.onItemLongClick(location.id, bindingAdapterPosition)

            false
        }
    }

    private fun prepareLocation(location: LocationVO) {
        viewBinding.cardSummaryTitle.text = location.title
        viewBinding.cardSummaryVisitedCount.root.isGone = true
        val (year, month, day) = location.visitedAt.split("-")
        viewBinding.cardSummaryVisitedAt.layoutSummaryItemBody.text = viewBinding.root.context.getString(R.string.card_visited_at, year, month.trim('0'), day.trim('0'))

        if (location.roadAddress.isNotEmpty()) {
            viewBinding.cardSummaryAddress.root.isVisible = true
            viewBinding.cardSummaryAddress.layoutSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
            viewBinding.cardSummaryAddress.layoutSummaryItemBody.text = location.roadAddress
        } else { viewBinding.cardSummaryAddress.root.isGone = true }

        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardSummaryMarkerEmoji.root.isVisible = true
            viewBinding.cardSummaryMarkerEmoji.layoutSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
            viewBinding.cardSummaryMarkerEmoji.layoutSummaryItemBody.text = location.markerEmoji
        } else { viewBinding.cardSummaryMarkerEmoji.root.isGone = true }
    }

    private fun prepareTags(tags: List<TagVO>) {
        val tagTest = tags.withIndex().joinToString { it.value.body }
        if (tagTest.isNotEmpty()) {
            viewBinding.cardSummaryTags.root.isVisible = true
            viewBinding.cardSummaryTags.layoutSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
            viewBinding.cardSummaryTags.layoutSummaryItemBody.text = tagTest
        } else { viewBinding.cardSummaryTags.root.isGone = true }
    }

    private fun prepareFriends(friends: List<FriendVO>) {
        val friendText = friends.joinToString { it.nickname!! }
        if (friendText.isNotEmpty()) {
            viewBinding.cardSummaryFriends.root.isVisible = true
            viewBinding.cardSummaryFriends.layoutSummaryItemIcon.setImageResource(R.drawable.round_group_12)
            viewBinding.cardSummaryFriends.layoutSummaryItemBody.text = friendText
        } else { viewBinding.cardSummaryFriends.root.isGone = true }
    }
}