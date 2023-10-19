package studio.hcmc.reminisce.ui.activity.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardTagDetailSummaryBinding
import studio.hcmc.reminisce.vo.location.LocationVO

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

    fun bind(location: LocationVO) {
        viewBinding.cardTagDetailSummaryVisitedAt.cardCategoryDetailSummaryItemTitle.text = location.visitedAt

        // address
        viewBinding.cardTagDetailSummaryAddress.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
        val addressBuilder = StringBuilder()
        addressBuilder.append(location.latitude)
        addressBuilder.append(location.longitude)
        viewBinding.cardTagDetailSummaryAddress.cardCategoryDetailSummaryItemTitle.text = addressBuilder.toString()

        // hashtag
        viewBinding.cardTagDetailSummaryHashtag.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_tag_12)

        // click
        viewBinding.root.setOnClickListener { delegate.onItemClick(location.id) }
    }
}