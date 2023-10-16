package studio.hcmc.reminisce.ui.activity.category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType

class CategoryDetailAdapter(
    private val summaryDelegate: SummaryViewHolder.Delegate,
    private val categoryHeaderDelegate: CategoryDetailHeaderViewHolder.Delegate,
) : Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        0 -> CategoryDetailHeaderViewHolder(parent, categoryHeaderDelegate)
        1 -> SummaryViewHolder(parent, summaryDelegate)
        2 -> DateSeparatorViewHolder(parent)
//        2 -> SummaryItemViewHolder(parent)
        else -> unknownViewType(viewType)
    }

//    override fun getItemCount() = categoryDetailContents.size
    override fun getItemCount() = summaryDelegate.locations.size + 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CategoryDetailHeaderViewHolder -> holder.bind()
            is SummaryViewHolder -> holder.bind(summaryDelegate.locations[position - 1])
            is DateSeparatorViewHolder -> holder.bind()
//            is SummaryItemViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val locationSize = summaryDelegate.locations.size
        return when (position) {
            0 -> 0
            in 1..locationSize -> 1
            locationSize + 1 -> 2

            else -> throw AssertionError()
        }
    }


}
/*
override fun getItemViewType(position: Int): Int {
        val categorySize = categoryDelegate.categories.size
        return when (position) {
            0 -> 0
            in 1..categorySize -> 1
            categorySize + 1 -> 2
            categorySize + 2 -> 3
            categorySize + 3 -> 4
            else -> throw AssertionError()
        }
    }
 */