package studio.hcmc.reminisce.ui.activity.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardTagDetailSummaryBinding

class TagDetailSummaryViewHolder(
    private val viewBinding: CardTagDetailSummaryBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(locationId: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardTagDetailSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: TagDetailAdapter.TagContents) {
        val summaryContent = (content as? TagDetailAdapter.TagDetailContent)?.location ?: return
        val addressBuilder = StringBuilder()
        addressBuilder.append(summaryContent.latitude)
        addressBuilder.append(summaryContent.longitude)

        viewBinding.apply {
            cardTagDetailSummaryVisitedAt.cardCategoryDetailSummaryItemTitle.text = summaryContent.visitedAt
            cardTagDetailSummaryAddress.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
            cardTagDetailSummaryAddress.cardCategoryDetailSummaryItemTitle.text = addressBuilder.toString()
        }
        viewBinding.root.setOnClickListener { delegate.onItemClick(summaryContent.id) }
    }
}