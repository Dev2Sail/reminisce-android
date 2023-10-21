package studio.hcmc.reminisce.ui.activity.category.editable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCategoryEditableDetailSummaryBinding
import studio.hcmc.reminisce.ui.activity.category.SummaryModal

class EditableSummaryViewHolder(
    private val viewBinding: CardCategoryEditableDetailSummaryBinding,
//    private val delegate: Delegate,
    private val selectedLocationIds: HashSet<Int> = HashSet()

): ViewHolder(viewBinding.root) {
//    interface Delegate: SingleTypeAdapterDelegate<LocationVO> {
//
//
//    }

//    constructor(parent: ViewGroup, delegate: Delegate): this(
//        viewBinding = CardCategoryEditableDetailSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
//        delegate = delegate
//    )

    constructor(parent: ViewGroup): this(
        viewBinding = CardCategoryEditableDetailSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(summary: SummaryModal) {
        viewBinding.apply {
            cardCategoryDetailSummaryTitle.text = summary.title
            cardCategoryDetailSummaryVisitedAt.cardCategoryDetailSummaryItemTitle.text = summary.visitedAt

            cardCategoryDetailSummaryAddress.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_location_on_12)
            val addressBuilder = StringBuilder()
            addressBuilder.append(summary.latitude)
            addressBuilder.append(summary.longitude)
            cardCategoryDetailSummaryAddress.cardCategoryDetailSummaryItemTitle.text = addressBuilder.toString()

            cardCategoryDetailSummaryVisitCount.cardCategoryDetailSummaryItemIcon.setImageResource(R.drawable.round_push_pin_12)
//            cardCategoryDetailSummaryVisitCount.cardCategoryDetailSummaryItemTitle.text = "~시 ~구 ${summary.visitedCount}번째 방문"
            cardCategoryDetailSummaryVisitCount.cardCategoryDetailSummaryItemTitle.text = "~시 ~구 n번째 방문"
        }

        viewBinding.root.setOnClickListener {
            viewBinding.cardCategoryDetailSummaryCheckbox.isChecked = !viewBinding.cardCategoryDetailSummaryCheckbox.isChecked
            if (!selectedLocationIds.add(summary.id)) {
                selectedLocationIds.remove(summary.id)
            }
        }

//        viewBinding.cardCategoryDetailSummaryCheckbox.addOnCheckedStateChangedListener { checkBox, state ->
//
//        }
    }
}