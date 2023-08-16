package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCategoryDetailSummaryBinding
import studio.hcmc.reminisce.vo.location.LocationVO

class SummaryViewHolder(
    private val viewBinding: CardCategoryDetailSummaryBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        val summaryList: List<LocationVO>

        fun onSummaryClick(summary: LocationVO)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardCategoryDetailSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind() {
        // category.title = summaryTitle
        viewBinding.cardCategoryDetailSummaryTitle.text = "Example Text"
        val checkBox = viewBinding.cardCategoryDetailSummaryCheckbox.isVisible
        val visitedAtIcon = viewBinding.cardCategoryDetailSummaryVisitedAt.cardCategoryDetailSummaryItemIcon
        var visitedAtText = viewBinding.cardCategoryDetailSummaryVisitedAt.cardCategoryDetailSummaryItemTitle.text
        val addressIcon = viewBinding.cardCategoryDetailSummaryAddress.cardCategoryDetailSummaryItemIcon
        var addressText = viewBinding.cardCategoryDetailSummaryAddress.cardCategoryDetailSummaryItemTitle.text
        val visitCountIcon = viewBinding.cardCategoryDetailSummaryVisitCount.cardCategoryDetailSummaryItemIcon
        var visitCountText = viewBinding.cardCategoryDetailSummaryVisitCount.cardCategoryDetailSummaryItemTitle.text


        visitedAtText = System.currentTimeMillis().toString()

        addressIcon.setImageResource(R.drawable.round_location_on_12)
        addressText = "인천광역시 어쩌고 저쩌고"

        visitCountIcon.setImageResource(R.drawable.round_push_pin_12)
        visitCountText = "인천광역시 미추홀구 4번째 방문"

        viewBinding.root.setOnClickListener {
            Toast.makeText(it.context, "summary clicked", Toast.LENGTH_SHORT).show()
        }
    }
}


/*
default : visitedAt, address, count
options : group, tag
 */