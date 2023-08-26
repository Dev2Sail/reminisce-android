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
        val locations: List<LocationVO>

        fun onSummaryClick(location: LocationVO)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardCategoryDetailSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(location: LocationVO) {
        viewBinding.cardCategoryDetailSummaryTitle.text = location.title
        val checkBox = viewBinding.cardCategoryDetailSummaryCheckbox.isVisible

        // visitedAt
        viewBinding.cardCategoryDetailSummaryVisitedAt.cardCategoryDetailSummaryItemTitle.text = location.createdAt.toString()
        // address
        viewBinding.cardCategoryDetailSummaryAddress.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
        val addressBuilder = StringBuilder()
        addressBuilder.append("latitude: ")
        addressBuilder.append(location.latitude)
        addressBuilder.append(" longitude ")
        addressBuilder.append(location.longitude)
        viewBinding.cardCategoryDetailSummaryAddress.cardCategoryDetailSummaryItemTitle.text = addressBuilder.toString()
        // visitedCount
        viewBinding.cardCategoryDetailSummaryVisitCount.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_push_pin_12)
        viewBinding.cardCategoryDetailSummaryVisitCount.cardCategoryDetailSummaryItemTitle.text = "~시 ~구 n번째 방문"


        viewBinding.root.setOnClickListener {
            Toast.makeText(it.context, "clicked: ${location.id}", Toast.LENGTH_SHORT).show()
        }
    }
}