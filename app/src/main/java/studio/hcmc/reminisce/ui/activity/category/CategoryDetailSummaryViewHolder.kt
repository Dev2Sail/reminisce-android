package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardSummaryBinding
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.user.UserVO

class CategoryDetailSummaryViewHolder(
    private val viewBinding: CardSummaryBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(location: LocationVO)
        fun getUser(userId: Int): UserVO
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: CategoryDetailAdapter.DetailContent) {
        val (location, tags, friends) = content

        if (location != null) {
            viewBinding.root.isVisible = true
            val addressBuilder = StringBuilder()
            // TODO 좌표 -> 주소로 변환 후 getString에서 정해둔 포맷으로 넣으셈! (date_separator) 참고
            addressBuilder.append(location.latitude)
            addressBuilder.append(location.longitude)

            val (year, month, day) = location.visitedAt.split("-")
            viewBinding.apply {
                cardSummaryTitle.text = location.title
                cardSummaryVisitedAt.layoutSummaryItemBody.text = viewBinding.root.context.getString(R.string.card_visited_at, year, month.trim('0'), day.trim('0'))
                cardSummaryAddress.layoutSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
                cardSummaryAddress.layoutSummaryItemBody.text = addressBuilder.toString()
                cardSummaryVisitedCount.root.isGone = true
            }

            if (!location.markerEmoji.isNullOrEmpty()) {
                viewBinding.cardSummaryMarkerEmoji.root.isVisible = true
                viewBinding.cardSummaryMarkerEmoji.apply {
                    layoutSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
                    layoutSummaryItemBody.text = location.markerEmoji
                }
            } else {
                viewBinding.cardSummaryMarkerEmoji.root.isGone = true
            }

            viewBinding.root.setOnClickListener {
                delegate.onItemClick(location)
            }
        } else {
            viewBinding.root.isGone = true
        }

        if (!tags.isNullOrEmpty()) {
            val tagText = tags.withIndex().joinToString { it.value.body }
            viewBinding.cardSummaryTags.root.isVisible = true
            viewBinding.cardSummaryTags.apply {
                layoutSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
                layoutSummaryItemBody.text = tagText
            }
        } else {
            viewBinding.cardSummaryTags.root.isGone = true
        }

        if (!friends.isNullOrEmpty()) {
            val friendText = friends.joinToString { it.nickname ?: delegate.getUser(it.opponentId).nickname }
            viewBinding.cardSummaryFriends.root.isVisible = true
            viewBinding.cardSummaryFriends.apply {
                layoutSummaryItemIcon.setImageResource(R.drawable.round_group_12)
                layoutSummaryItemBody.text = friendText
            }
        } else {
            viewBinding.cardSummaryFriends.root.isGone = true
        }
    }
}