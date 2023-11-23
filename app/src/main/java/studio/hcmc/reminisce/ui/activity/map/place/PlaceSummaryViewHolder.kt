package studio.hcmc.reminisce.ui.activity.map.place

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
import studio.hcmc.reminisce.vo.user.UserVO

class PlaceSummaryViewHolder(
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

    fun bind(content: PlaceAdapter.DetailContent) {
        val (location, tags, friends) = content
        prepareLocation(location)
        prepareTags(tags)
        prepareFriends(friends)
        viewBinding.root.setOnClickListener { delegate.onItemClick(location.id, location.title) }
    }

    private fun prepareLocation(location: LocationVO) {
        viewBinding.cardSummaryTitle.text = location.title
        viewBinding.cardSummaryVisitedCount.root.isGone = true
        val (year, month, day) = location.visitedAt.split("-")
        viewBinding.cardSummaryVisitedAt.layoutSummaryItemBody.text = viewBinding.root.context.getString(R.string.card_visited_at, year, month.trim('0'), day.trim('0'))

        if (location.roadAddress.isNotEmpty()) {
            viewBinding.cardSummaryAddress.apply {
                root.isVisible = true
                layoutSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
                layoutSummaryItemBody.text = location.roadAddress
            }
        } else { viewBinding.cardSummaryAddress.root.isGone = true }

        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardSummaryMarkerEmoji.apply {
                root.isVisible = true
                layoutSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
                layoutSummaryItemBody.text = location.markerEmoji
            }
        } else { viewBinding.cardSummaryMarkerEmoji.root.isGone = true }
    }

    private fun prepareTags(tags: List<TagVO>) {
        val tagTest = tags.withIndex().joinToString { it.value.body }
        if (tagTest.isNotEmpty()) {
            viewBinding.cardSummaryTags.apply {
                root.isVisible = true
                layoutSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
                layoutSummaryItemBody.text = tagTest
            }
        } else { viewBinding.cardSummaryTags.root.isGone = true }
    }

    private fun prepareFriends(friends: List<FriendVO>) {
        val friendText = friends.joinToString { it.nickname ?: delegate.getUser(it.opponentId).nickname }
        if (friendText.isNotEmpty()) {
            viewBinding.cardSummaryFriends.apply {
                root.isVisible = true
                layoutSummaryItemIcon.setImageResource(R.drawable.round_group_12)
                layoutSummaryItemBody.text = friendText
            }
        } else { viewBinding.cardSummaryFriends.root.isGone = true }
    }
}