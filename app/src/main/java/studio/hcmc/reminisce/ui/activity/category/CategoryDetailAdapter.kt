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
        1 -> DateSeparatorViewHolder(parent)
        2 -> SummaryItemViewHolder(parent)
        3 -> SummaryViewHolder(parent, summaryDelegate)
        else -> unknownViewType(viewType)
    }

//    override fun getItemCount() = categoryDetailContents.size
    override fun getItemCount() = 4

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CategoryDetailHeaderViewHolder -> holder.bind()
            is SummaryViewHolder -> holder.bind()
            is DateSeparatorViewHolder -> holder.bind()
            is SummaryItemViewHolder -> holder.bind()
        }
    }

//    override fun getItemViewType(position: Int) = when (defaultCategoryContent[position]) {
//        is DefaultCategoryHeader -> 0
//        is DefaultSeparator -> 1
//        is DefaultSummary -> 2
//        is DefaultSummaryItem -> 3
//        else -> unknownViewType(position)
//    }
}
