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
    }

    constructor(parent: ViewGroup, delegate: Delegate ): this(
        viewBinding = CardCheckableSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: CategoryEditableDetailAdapter.DetailContent) {
        val (location, tags, friends) = content

        val addressBuilder = StringBuilder()
        // TODO 좌표 -> 주소로 변환 후 getString에서 정해둔 포맷으로 넣으셈! (date_separator) 참고
        addressBuilder.append(location.latitude)
        addressBuilder.append(location.longitude)

        val (year, month, day) = location.visitedAt.split("-")
        viewBinding.apply {
            cardCheckableSummaryTitle.text = location.title
            cardCheckableSummaryVisitedAt.layoutSummaryItemBody.text = viewBinding.root.context.getString(R.string.card_visited_at, year, month.trim('0'), day.trim('0'))
            cardCheckableSummaryVisitedCount.root.isGone = true
            cardCheckableSummaryAddress.layoutSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
            cardCheckableSummaryAddress.layoutSummaryItemBody.text = addressBuilder.toString()
        }

        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardCheckableSummaryMarkerEmoji.root.isVisible = true
            viewBinding.cardCheckableSummaryMarkerEmoji.apply {
                layoutSummaryItemIcon.setImageResource(R.drawable.round_add_reaction_12)
                layoutSummaryItemBody.text = location.markerEmoji
            }
        } else {
            viewBinding.cardCheckableSummaryMarkerEmoji.root.isGone = true
        }

        val tagText = tags.withIndex().joinToString { it.value.body }
        if (tagText.isNotEmpty()) {
            viewBinding.cardCheckableSummaryTags.root.isVisible = true
            viewBinding.cardCheckableSummaryTags.apply {
                layoutSummaryItemIcon.setImageResource(R.drawable.round_tag_12)
                layoutSummaryItemBody.text = tagText
            }
        } else {
            viewBinding.cardCheckableSummaryTags.root.isGone = true
        }

        val friendText = friends.joinToString { it.nickname ?: delegate.getUser(it.opponentId).nickname }
        if (friendText.isNotEmpty()) {
            viewBinding.cardCheckableSummaryFriends.root.isVisible = true
            viewBinding.cardCheckableSummaryFriends.apply {
                layoutSummaryItemIcon.setImageResource(R.drawable.round_group_12)
                layoutSummaryItemBody.text = friendText
            }
        } else {
            viewBinding.cardCheckableSummaryFriends.root.isGone = true
        }

        viewBinding.cardCheckableSummaryCheckbox.setOnClickListener {
            delegate.onItemClick(location.id)
        }

        viewBinding.cardCheckableSummaryContainer.setOnClickListener {
            viewBinding.cardCheckableSummaryCheckbox.isChecked = delegate.onItemClick(location.id)
        }
    }
}